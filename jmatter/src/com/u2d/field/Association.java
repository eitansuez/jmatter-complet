/*
 * Created on Feb 17, 2004
 */
package com.u2d.field;

import java.awt.datatransfer.*;
import javax.swing.event.*;
import java.beans.*;
import java.util.List;
import com.u2d.app.*;
import com.u2d.element.Field;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.pubsub.*;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationNotifier;

/**
 * @author Eitan Suez
 */
public class Association implements Transferable, java.io.Serializable, ValidationNotifier
{
   protected Field _field;
   protected ComplexEObject _parent;
   
   public Association(Field field, ComplexEObject parent)
   {
      _field = field;
      _parent = parent;
   }
   
   public Title title()
   {
      return _parent.title().append(_field.getLabel());
   }
   public String toString() { return title().toString(); }
   
   // conveniences..
   
   public ComplexEObject get()
   {
      return (ComplexEObject) _field.get(_parent);
   }
   // obviously this class needs a lot of work..
   // what is needed: two types of associations: tomany and toone, with polymorphic get
   public AbstractListEO getAsList()
   {
      return (AbstractListEO) _field.get(_parent);
   }
   
   public void set(final ComplexEObject value)
   {
      //speaks for itself, but in prose:  if transient delay set until after save
      if (value != null && value.isTransientState())
      {
         value.addAppEventListener("ONCREATE", new AppEventListener()
            {
               public void onEvent(AppEvent evt)
               {
                  _field.set(_parent, value);
               }
            });
      }
      else
      {
         _field.set(_parent, value);
      }
   }
   
   public void associateList(final List value)
   {
      _field.set(_parent, value);
      if (!_parent.isEditableState()) _parent.save();
   }
   
   public void associate(ComplexEObject value)
   {
      set(value);
      if (!_parent.isEditableState()) _parent.save();
   }
   
   public void dissociate()
   {
      ComplexEObject oldValue = get();
      
      set(null);

      if (!_parent.isEditableState())
      {
         AppFactory.getInstance().getApp().getPersistenceMechanism().updateAssociation(_parent, oldValue);
      }
   }
   
   public void dissociateItem(ComplexEObject eo)
   {
      AbstractListEO list = (AbstractListEO) _field.get(_parent);
      list.remove(eo);
      if (!_parent.isEditableState())
      {
         _parent.save();
      }
   }
   
   public Field field() { return _field; }
   public ComplexEObject parent() { return _parent; }
   
   public javax.swing.Icon iconSm() { return _field.fieldtype().iconSm(); }
   public boolean isEmpty() { return _field.isEmpty(_parent); }
   public ComplexType type() { return _field.fieldtype(); }
   
   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      _parent.addPropertyChangeListener(listener);
   }
   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      _parent.removePropertyChangeListener(listener);
   }
   public void addChangeListener(ChangeListener l)
   {
      _parent.addChangeListener(l);
   }
   public void removeChangeListener(ChangeListener l)
   {
      _parent.removeChangeListener(l);
   }
   
   public boolean isEditableState()
   {
      return _parent.isEditableState();
   }
   
   public String getName() { return _field.name(); }
   
   
   // ========== implementation of Transferrable Interface  ===============
   
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
   {
      if (!isDataFlavorSupported(flavor))
          throw new UnsupportedFlavorException(flavor);
      return this;
   }
   
   public DataFlavor[] getTransferDataFlavors()
   {
      DataFlavor typeFlavor = makeFlavor(type().getJavaClass());
      return new DataFlavor[] { FLAVOR, typeFlavor };
   }
   
   public boolean isDataFlavorSupported(DataFlavor f)
   {
      return f.equals(FLAVOR);
   }

   public static DataFlavor FLAVOR;
   static
   {
      FLAVOR = makeFlavor(Association.class);
   }
   
   private static DataFlavor makeFlavor(Class cls)
   {
      try
      {
         String flavorType = DataFlavor.javaJVMLocalObjectMimeType + 
         ";class="+cls.getName();
         return new DataFlavor(flavorType);
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         throw new RuntimeException("Failed to find class while attempting "+ 
               "to construct a data flavor for it! ("+cls+")");
      }
   }

   /* ** Validation Exception Notification Code ** */
   protected transient ValidationEvent _validationEvent = null;
   protected transient EventListenerList _validationListenerList = new EventListenerList();
   public void addValidationListener(ValidationListener l)
   {
     _validationListenerList.add(ValidationListener.class, l);
   }
   public void removeValidationListener(ValidationListener l)
   {
     _validationListenerList.remove(ValidationListener.class, l);
   }
   public void fireValidationException(String errorMsg, boolean statusType)
   {
      Object[] listeners = _validationListenerList.getListenerList();
      for (int i = listeners.length-2; i>=0; i-=2)
      {
         if (listeners[i]==ValidationListener.class)
         {
            _validationEvent = new ValidationEvent(this, errorMsg, statusType);
            ((ValidationListener)listeners[i+1]).validationException(_validationEvent);
         }
      }
   }
   public void fireValidationException(String errorMsg)
   {
      fireValidationException(errorMsg, false);
   }
   
}
