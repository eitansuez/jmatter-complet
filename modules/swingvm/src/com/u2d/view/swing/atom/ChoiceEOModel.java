package com.u2d.view.swing.atom;

import com.u2d.type.atom.ChoiceEO;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 14, 2007
 * Time: 5:43:35 PM
 */
public class ChoiceEOModel implements ComboBoxModel
{
   private Object selectedItem;
   private ChoiceEO sample;

   public ChoiceEOModel(ChoiceEO eo)
   {
      selectedItem = eo;
      sample = eo;
   }

   public Object getSelectedItem() { return selectedItem; }

   public void setSelectedItem(Object anItem)
   {
      if (anItem.equals(selectedItem)) return;
      selectedItem = anItem;
      fireContentsChanged(this, -1, -1);
   }

   public Object getElementAt(int index)
   {
      if (index >= sample.entries().size())
      {
         throw new IllegalArgumentException("Invalid index value: "+index);
      }
      if (sample.entries() instanceof List)
      {
         return ((List) sample.entries()).get(index);
      }
      
      Iterator itr = sample.entries().iterator();
      int i=0;
      Object value = itr.next();
      while (i < index)
      {
         value = itr.next();
         i++;
      }
      return value;
   }

   public int getSize()
   {
      return sample.entries().size();
   }

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

}
