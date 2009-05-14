/*
 * Created on Apr 29, 2005
 */
package com.u2d.view.swing;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.AtomicEObject;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationNotifier;
import com.u2d.validation.Required;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * @author Eitan Suez
 */
public class ValidationNoticePanel extends JLabel implements ValidationListener
{
   private ValidationNotifier _target;
   private boolean _listening = false;

   public ValidationNoticePanel()
   {
      setText("");
      ComponentStyle.addClass(this, "validation-msg");
   }
   public ValidationNoticePanel(ValidationNotifier target, boolean startListening)
   {
      this();
      _target = target;
      if (startListening) startListening();
   }
   public ValidationNoticePanel(ValidationNotifier target, ComplexEObject ceo)
   {
      this(target, ceo.isEditableState());
   }

   public void setTarget(ValidationNotifier notifier)
   {
      stopListening();
      _target = notifier;
      startListening();
   }
   
   public synchronized void startListening()
   {
      if (_listening) return;
      
      _target.addValidationListener(this);
      _listening = true;
   }
   public synchronized void stopListening()
   {
      if (_target != null)
      {
         _target.removeValidationListener(this);
      }
      _listening = false;
   }
   
   public void reset()
   {
      setText("");
   }
   
   public void validationException(final ValidationEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (!Required.MSG.equals(evt.getMsg()) ||
                  !sourceIsAtomic(evt.getSource()))
            {
               setText(evt.getMsg());
            }
         }

         private boolean sourceIsAtomic(Object source)
         {
            return (source instanceof EObject) &&
                  ( ((EObject) source) instanceof AtomicEObject );
         }
      });
   }

}

