package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.StringEO;
import com.u2d.view.ActionNotifier;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
import com.u2d.element.Field;

import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 12:51:34 PM
 *
 * @author Eitan Suez
 */
public class StringEditor extends JTextField implements AtomicEditor, ActionNotifier, ValidationListener
{
   public StringEditor()
   {
      super(12);
   }
   
   public void render(AtomicEObject value)
   {
      if (value.field() != null)
      {
         Field field = value.field();
         if (field.displaysize() > 0 && field.displaysize() != getColumns())
         {
            setColumns(value.field().displaysize());
         }
         if (field.colsize() > 0 && (!(getDocument() instanceof MaxLength)))
         {
            setDocument(new MaxLength(field.colsize()));
         }
      }

      StringEO eo = (StringEO) value;
      if (!getText().equals(eo.stringValue()))
         setText(eo.stringValue());
   }
   public int bind(AtomicEObject value)
   {
      StringEO eo = (StringEO) value;
      eo.setValue(getText());
      return 0;
   }

   public void passivate()
   {
      setText("");
   }

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
