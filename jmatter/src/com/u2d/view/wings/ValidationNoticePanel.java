package com.u2d.view.wings;

import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationNotifier;
import com.u2d.validation.ValidationEvent;
import com.u2d.model.ComplexEObject;
import org.wings.SLabel;
import org.wings.SFont;
import java.awt.Color;

/**
 * @author Eitan Suez
 */
public class ValidationNoticePanel extends SLabel implements ValidationListener
{
   public static SFont ITALIC_FONT;
   static
   {
      ITALIC_FONT = new SFont();
      ITALIC_FONT.setStyle(SFont.ITALIC);
   }

   ValidationNotifier _target;

   ValidationNoticePanel(ValidationNotifier target, ComplexEObject ceo)
   {
      _target = target;

      setText("");
      setFont(ITALIC_FONT);
      setForeground(Color.RED);

      if (ceo.isEditableState())
         startListening();
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
      setText(evt.getMsg());
   }

}
