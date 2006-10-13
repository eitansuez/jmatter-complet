/*
 * Created on Jan 19, 2004
 */
package com.u2d.model;

import com.u2d.app.*;
import com.u2d.element.Field;
import com.u2d.pattern.*;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.view.*;
import javax.swing.event.*;

/**
 * @author Eitan Suez
 */
public abstract class AbstractEObject
                      implements java.io.Serializable, EObject
{
   public abstract Title title();
   public abstract boolean isEmpty();
   public abstract int validate();

   public abstract EView getView();
   public abstract Onion commands();

   private Field _field = null;
   private ComplexEObject _parent;

   public void setField(Field field, ComplexEObject parent)
   {
      _field = field;
      _parent = parent;
   }
   public Field field() { return _field; }
   public ComplexEObject parentObject() { return _parent; }

   public abstract EObject makeCopy();
   public abstract void setValue(EObject value);

   
   private AppSession appSession() { return Context.getInstance().getAppSession(); }
   protected Application app() { return Context.getInstance().getApplication(); } 
   protected ViewMechanism vmech() { return Context.getInstance().getViewMechanism(); } 
   protected PersistenceMechanism persistor() { return Context.getInstance().getPersistenceMechanism(); }

   protected HBMPersistenceMechanism hbmPersistor()
   {
      if (!(persistor() instanceof HBMPersistenceMechanism))
      {
         throw new RuntimeException("Not running a Hibernate Persistence Mechanism");
      }
      return (HBMPersistenceMechanism) persistor();
   }
   protected User currentUser() { return appSession().getUser(); }


   /* ** State Change Support Code ** */
   protected transient ChangeEvent changeEvent = null;
   protected transient EventListenerList _listenerList = new EventListenerList();
   protected transient EventListenerList _postListeners = new EventListenerList();

   public void addChangeListener(ChangeListener l)
   {
      _listenerList.add(ChangeListener.class, l);
   }
   public void addPostChangeListener(ChangeListener l)
   {
      _postListeners.add(ChangeListener.class,  l);
   }

   public void removeChangeListener(ChangeListener l)
   {
      _listenerList.remove(ChangeListener.class, l);
   }
   public void removePostChangeListener(ChangeListener l)
   {
      _postListeners.remove(ChangeListener.class, l);
   }

   // forced public after package restructuring..TODO: think why called from outside
   public void fireStateChanged()
   {
      fireChange(_listenerList.getListenerList());
      fireChange(_postListeners.getListenerList());
   }

   private void fireChange(Object[] listeners)
   {
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i]==ChangeListener.class)
         {
            if (changeEvent == null)
               changeEvent = new ChangeEvent(this);
            ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
         }
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
      /*
       * explanation: formview binds various validationnotice panels for each
       * property of an object. if the property is a compositelist, each element
       * in the list is validated but each doesn't have its own validationnoticepanel
       * so just send a message to the parent's validation notice panel
       */
      if (field() != null && field().isComposite() && field().isIndexed())
      {
         field().get(parentObject()).fireValidationException(errorMsg, false);
      }
      fireValidationException(errorMsg, false);
   }

}
