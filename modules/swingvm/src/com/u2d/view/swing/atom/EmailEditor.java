package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.Email;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:43:52 PM
 *
 * @author Eitan Suez
 */
public class EmailEditor extends JTextField implements AtomicEditor, ValidationListener
{
   public EmailEditor()
   {
      setColumns(15);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      ((Email) value).parseValue(getText());
      return 0;
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
