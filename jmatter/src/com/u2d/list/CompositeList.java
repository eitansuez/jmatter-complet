/*
 * Created on Mar 9, 2004
 */
package com.u2d.list;

import com.u2d.model.ComplexEObject;
import com.u2d.model.Harvester;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.pattern.State;
import com.u2d.pattern.Block;
import com.u2d.app.Tracing;
import java.util.Iterator;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * @author Eitan Suez
 */
public class CompositeList extends SimpleListEO
{
   private transient boolean _fixedSize = false;
   private ComplexEObject _parent;
   private String _parentFldname;
   private Method _setterMethod;

   private transient Logger _tracer = Tracing.tracer();

   private CompositeList() {}  // for jibx

   public CompositeList(Class clazz) { super(clazz); }
   public CompositeList(Class clazz, ComplexEObject parent, String parentFldname)
   {
      this(clazz);
      workoutSetter(parent, parentFldname);
   }

   private void workoutSetter(ComplexEObject parent, String parentFldname)
   {
      _parent = parent;
      _parentFldname = parentFldname;
      try
      {
         PropertyDescriptor descriptor = new PropertyDescriptor(_parentFldname, _clazz, null,
                                                                Harvester.makeSetterName(_parentFldname));
         _setterMethod = descriptor.getWriteMethod();
      }
      catch (IntrospectionException e)
      {
         String msg = String.format("failed to find write method for field %s on type %s", _parentFldname, _clazz);
         _tracer.info(msg);
         e.printStackTrace();
      }
   }

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

   public ComplexEObject addNew()
   {
      ComplexEObject instance = type().instance();
      add(instance);
      setParent(instance);
      return instance;
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
            setParent(ceo);
            ceo.onLoad();
         }
      });
   }

   private void setParent(ComplexEObject ceo)
   {
      if (_setterMethod != null && _parent != null)
      {
         try
         {
            _setterMethod.invoke(ceo, _parent);
         }
         catch (IllegalAccessException e)
         {
            _tracer.info("failed to set back relationship for "+ceo+" in composite list "+field());
            e.printStackTrace();
         }
         catch (InvocationTargetException e)
         {
            _tracer.info("failed to set back relationship for "+ceo+" in composite list "+field());
            e.printStackTrace();
         }
      }
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
