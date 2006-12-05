/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.TimeEO;

import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class TimeEditor extends JTextField implements AtomicEditor
{
   private boolean _formatSet = false;

   public TimeEditor()
   {
      setColumns(6);
      setHorizontalAlignment(JTextField.RIGHT);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
      if (!_formatSet)
      {
         TimeEO eo = (TimeEO) value;
         eo.formatter().toPattern();
         setToolTipText("["+eo.formatter().toPattern()+"]");
         _formatSet = true;
      }
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
