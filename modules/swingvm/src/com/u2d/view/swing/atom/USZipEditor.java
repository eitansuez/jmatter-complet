package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:58:59 PM
 *
 * @author Eitan Suez
 */
public class USZipEditor extends JTextField implements AtomicEditor, ValidationListener
{
   public USZipEditor()
   {
      setColumns(9);
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
   
   public void passivate() { setText(""); }
   
   public void validationException(ValidationEvent evt)
   {
      AtomicView.colorBackground(this, evt);
   }
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      AtomicView.decorateComponentForValidation(g, this, false);
   }
}
