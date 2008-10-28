/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class SSNEditor extends JTextField implements AtomicEditor, ValidationListener
{
   public SSNEditor()
   {
      setColumns(11);
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
      AtomicView.toggleValidationClientProperty(this, evt);
   }
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      AtomicView.decorateComponentForValidation(g, this, false);
   }
   protected void paintBorder(Graphics g)
   {
      super.paintBorder(g);
      AtomicView.decorateBorderForValidation(g, this);
   }
}
