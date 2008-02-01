package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 1, 2008
 * Time: 9:30:28 PM
 */
public class DegreeEditor extends JTextField implements AtomicEditor
{
   public DegreeEditor()
   {
      setColumns(10);
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
