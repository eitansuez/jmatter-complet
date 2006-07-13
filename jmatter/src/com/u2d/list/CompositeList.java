/*
 * Created on Mar 9, 2004
 */
package com.u2d.list;

import com.u2d.model.ComplexEObject;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.pattern.State;
import com.u2d.pattern.Block;
import java.util.Iterator;

/**
 * @author Eitan Suez
 */
public class CompositeList extends SimpleListEO
{
   private transient boolean _fixedSize = false;
   
   private CompositeList() {}  // for jibx
   
   public CompositeList(Class clazz) { super(clazz); }

   public void jibxAdd(Object obj)
   {
      if (!(obj instanceof ComplexEObject))
         throw new IllegalArgumentException("Cannot add object that is not a ComplexEObject");
      super.add((ComplexEObject) obj);
   }
   public java.util.Iterator jibxIterator()
   {
      return super.iterator();
   }
   
   public boolean isFixedSize() { return _fixedSize; }
   public void setFixedSize(boolean fs) { _fixedSize = fs; }


   public EView getView() { return getMainView(); }
   public EView getMainView() { return getListView(); }

   public ListEView getListView()
   {
      if (type().isChoice())
      {
         return getMultiChoiceView();
      }
      else if (_fixedSize)
      {
         return getTableView();
      }
      else
      {
         return vmech().getEditableListView(this);
      }
   }

   private ListEView getMultiChoiceView()
   {
      return vmech().getMultiChoiceView(this);
   }

   public void setState(final State state)
   {
      forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            ceo.setState(state);
         }
      });
   }
   
   public void forEach(Block block)
   {
      for (Iterator itr = _items.iterator(); itr.hasNext(); )
      {
         ComplexEObject ceo = (ComplexEObject) itr.next();
         block.each(ceo);
      }
   }
   
   public void addNew()
   {
      ComplexEObject instance = type().instance();
      add(instance);
   }

   public void onBeforeSave()
   {
      forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            ceo.onBeforeSave();
         }
      });
   }
   
   public void onLoad()
   {
      forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            ceo.onLoad();
         }
      });
   }
   
   public void clear()
   {
      ComplexEObject item;
      int size = _items.size();
      for (int i=0; i<size; i++)
      {
         item = (ComplexEObject) _items.get(i);
         item.removeAppEventListener("ONDELETE", this);
      }
      _items.clear();
      fireIntervalRemoved(this, 0, size);
   }
   
}
