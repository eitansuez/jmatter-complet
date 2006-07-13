package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:40:18 PM
 *
 * @author Eitan Suez
 */
public class PercentEditor extends JTextField implements AtomicEditor
{
   public PercentEditor()
   {
      setColumns(6);
      setHorizontalAlignment(JTextField.RIGHT);
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
}
