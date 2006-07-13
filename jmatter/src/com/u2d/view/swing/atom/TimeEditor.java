/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;

import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class TimeEditor extends JTextField implements AtomicEditor
{
   private final String INPUT_TIP = "[hh:mm]";

   public TimeEditor()
   {
      setColumns(6);
      setToolTipText(INPUT_TIP);
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
