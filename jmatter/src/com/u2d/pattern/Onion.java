/*
 * Created on Apr 27, 2004
 */
package com.u2d.pattern;

import com.u2d.model.ComplexEObject;
import javax.swing.event.ListDataListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class Onion implements ListChangeNotifier
{
   private List _list = new ArrayList();
   private Onion _innerLayer;
      
   public Onion() { _innerLayer = null; }
   public Onion(Onion innerLayer) { _innerLayer = innerLayer; }

   public void add(Object obj)
   {
      int index0 = _list.size();
      _list.add(obj);
      fireIntervalAdded(this, index0, index0 );
   }

   public void addAll(Collection c)
   {
      int index0 = _list.size();
      _list.addAll(c);
      fireIntervalAdded(this, index0, _list.size()-1);
   }
   
   public void remove(Object obj)
   {
      int index = _list.indexOf(obj);
      if (index >= 0)
      {
         _list.remove(index);
         fireIntervalRemoved(this, index, index);
      }
      else if (_innerLayer != null)
      {
         _innerLayer.remove(obj);
      }
   }
   
   public boolean contains(Object obj)
   {
      if (_list.contains(obj))
      {
         return true;
      }
      else if (_innerLayer != null)
      {
         return _innerLayer.contains(obj);
      }
      return false;
   }
   
   public boolean contains(SimpleFinder finder)
   {
      return (find(finder) != null);
   }

   public Iterator iterator() { return _list.iterator(); }
   public int size()
   {
      if (_innerLayer == null)
         return _list.size();
      else
         return _list.size() + _innerLayer.size();
   }
   public int numLayers()
   {
      if (_innerLayer == null)
         return 1;
      else
         return _innerLayer.numLayers() + 1;
   }
   
   public boolean isLastLayer()
   {
      return (_innerLayer == null) || (_innerLayer.size() == 0);
   }
   public boolean hasMoreLayers() { return !isLastLayer(); }
   public List getOuterLayer() { return _list; }
   public Onion getInnerLayer() { return _innerLayer; }
   
   public Object get(int index)
   {
      if (_list.size() > index)
         return _list.get(index);
      
      return _innerLayer.get(index - _list.size());
   }
   
   public void wrap(Onion innerLayer) { _innerLayer = innerLayer; }

   public boolean isEmpty() { return (size() == 0); }

   public Iterator deepIterator() { return new OnionIterator(this); }

   static class OnionIterator implements Iterator
   {
      Onion _current;
      Iterator _currentItr;
      
      OnionIterator(Onion onion)
      {
         _current = onion;
         _currentItr = _current.iterator();
      }
      public boolean hasNext()
      {
         return ( _currentItr.hasNext() ||
                 (_current.hasMoreLayers() && !_current.getInnerLayer().isEmpty())
               );
      }
      public Object next()
      {
         if (_currentItr.hasNext())
            return _currentItr.next();
         
         _current = _current.getInnerLayer();
         _currentItr = _current.iterator();
         return _currentItr.next();
      }
      public void remove()
      {
         _currentItr.remove();
      }
   }

   public void mergeIn(Onion onion)
   {
      Onion inner = this;
      while (inner.hasMoreLayers())
      {
         inner = inner.getInnerLayer();
      }
      inner.wrap(onion);
   }
   
   public Object find(SimpleFinder finder) { return find(finder, this); }
   
   public Onion filter(Filter filter)
   {
      OnionProcessor proc = new OnionProcessor(filter);
      new OnionPeeler(proc).peel(this);
      return proc.getResult();
   }
   
   class OnionProcessor implements Processor
   {
      private Onion currentOnion = new Onion();
      private Onion onion = currentOnion;
      private Filter filter;
      
      OnionProcessor(Filter filter)
      {
         this.filter = filter;
      }

      public void process(Object item)
      {
         if (filter.exclude(item)) return;
         currentOnion.add(item);
      }

      public void pause()
      {
         // need to be careful not to invert layers..
         Onion temp = currentOnion;
         currentOnion = new Onion();
         temp.mergeIn(currentOnion);
      }

      public void done()
      {
      }
      
      public Onion getResult()
      {
         return onion;
      }
   }

   public static Object find(SimpleFinder finder, Onion onion)
   {
      for (Iterator itr = onion.iterator(); itr.hasNext(); )
      {
         Object item = itr.next();
         if (finder.found(item))
            return item;
      }
      if (onion.hasMoreLayers())
         return find(finder, onion.getInnerLayer());
      else
         return null;
   }
   
   public Onion deepCopy()
   {
      List list = new ArrayList(_list);
      Onion copy = new Onion();
      copy.addAll(list);
      if (_innerLayer == null)
         return copy;
      else
         copy.wrap(_innerLayer.deepCopy());
      return copy;
   }

   public String toString()
   {
      String text = "{" + _list.toString();
      if (numLayers() == 1)
      {
         return text + "}";
      }
      else
      {
         return text + _innerLayer.toString() + "}";
      }
   }

   public Onion reduce()
   {
      Onion commands = this;
      while (commands.getOuterLayer().size() == 0 && commands.hasMoreLayers())
      {
         commands = commands.getInnerLayer();  // don't add an extraneous layer..
      }
      return commands;
   }
   
   // == listchangenotifier interface implementation..

   /* ** ===== List Change Support code ===== ** */
   
   protected transient EventListenerList _listDataListenerList = new EventListenerList();

   public void addListDataListener(ListDataListener l)
   {
      _listDataListenerList.add(ListDataListener.class, l);
   }

   public void removeListDataListener(ListDataListener l)
   {
      _listDataListenerList.remove(ListDataListener.class, l);
   }
   
   

   public void fireContentsChanged(Object source, int index0, int index1)
   {
      Object[] listeners = _listDataListenerList.getListenerList();
      ListDataEvent e = null;
      
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListDataListener.class) {
            if (e == null) {
               e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
            }
            ((ListDataListener)listeners[i+1]).contentsChanged(e);
         }         
      }
   }

   public void fireIntervalAdded(Object source, int index0, int index1)
   {
      Object[] listeners = _listDataListenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListDataListener.class) {
            if (e == null) {
               e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
            }
            ((ListDataListener)listeners[i+1]).intervalAdded(e);
         }
      }
   }

   public void fireIntervalRemoved(Object source, int index0, int index1)
   {
      Object[] listeners = _listDataListenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListDataListener.class) {
            if (e == null) {
               e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
            }
            ((ListDataListener)listeners[i+1]).intervalRemoved(e);
         }
      }
   }


   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof Onion)) return false;

      Onion onion = (Onion) obj;
      Iterator itr1 = deepIterator();
      Iterator itr2 = onion.deepIterator();
      while (itr1.hasNext() && itr2.hasNext())
      {
          Object o1 = itr1.next();
          Object o2 = itr2.next();
          if (!(o1==null ? o2==null : o1.equals(o2)))
         return false;
      }
      return !(itr1.hasNext() || itr2.hasNext());
   }

   public int hashCode()
   {
      int hashCode = 1;
      Iterator i = deepIterator();
      while (i.hasNext())
      {
         Object obj = i.next();
         hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
      }
      return hashCode;
   }
   
   public void forEach(Block block)
   {
      for (Iterator oitr = deepIterator(); oitr.hasNext(); )
      {
         block.each((ComplexEObject) oitr.next());
      }
   }

}
