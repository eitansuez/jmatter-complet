/*
 * Created on Apr 29, 2005
 */
package com.u2d.view.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.u2d.model.ComplexEObject;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationNotifier;

/**
 * @author Eitan Suez
 */
public class ValidationNoticePanel extends JLabel implements ValidationListener
{
   public static Font ITALIC_FONT;
   static
   {
      ITALIC_FONT = UIManager.getFont("Label.font").deriveFont(Font.ITALIC);
   }
   
   ValidationNotifier _target;
   
   ValidationNoticePanel(ValidationNotifier target, boolean startListening)
   {
      _target = target;
      
      setText("");
      setFont(ITALIC_FONT);
      setForeground(Color.RED);
      
      if (startListening)
         startListening();
   }
   ValidationNoticePanel(ValidationNotifier target, ComplexEObject ceo)
   {
      this(target, ceo.isEditableState());
   }
   
   void startListening()
   {
      _target.addValidationListener(this);
   }
   void stopListening()
   {
      _target.removeValidationListener(this);
   }
   
   void reset()
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

