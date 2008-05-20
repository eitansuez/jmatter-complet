/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;

import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class USPhoneEditor extends JTextField implements AtomicEditor, ValidationListener
{
   public USPhoneEditor()
   {
      setColumns(15);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      try
      {
         value.parseValue(getText());
         return 0;
      }
      catch (java.text.ParseException ex)
      {
         value.fireValidationException(ex.getMessage());
         return 1;
      }
   }

   public void passivate() { }

   public void validationException(ValidationEvent evt)
   {
      AtomicView.colorBackground(this, evt);
   }
}
