/*
 * Created on Apr 29, 2005
 */
package com.u2d.view.swing;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import com.u2d.model.ComplexEObject;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationNotifier;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * @author Eitan Suez
 */
public class ValidationNoticePanel extends JLabel implements ValidationListener
{
   private ValidationNotifier _target;
   
   public ValidationNoticePanel(ValidationNotifier target, boolean startListening)
   {
      _target = target;
      
      setText("");
      ComponentStyle.addClass(this, "validation-msg");
      
      if (startListening)
         startListening();
   }
   public ValidationNoticePanel(ValidationNotifier target, ComplexEObject ceo)
   {
      this(target, ceo.isEditableState());
   }
   
   public void startListening()
   {
      _target.addValidationListener(this);
   }
   public void stopListening()
   {
      _target.removeValidationListener(this);
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
            setText(evt.getMsg());
         }
      });
   }

}

