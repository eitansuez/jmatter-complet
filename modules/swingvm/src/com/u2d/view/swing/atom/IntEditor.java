package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.IntEO;
import com.u2d.ui.UIUtils;
import com.u2d.view.ActionNotifier;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:23:21 PM
 *
 * @author Eitan Suez
 */
public class IntEditor extends JTextField implements AtomicEditor, ActionNotifier
{
   public IntEditor()
   {
      //setFormatting();
      setColumns(4);
      setHorizontalAlignment(JTextField.RIGHT);
      UIUtils.selectOnFocus(this);
   }

//   private void setFormatting()
//   {
//      NumberFormatter formatter = new NumberFormatter();
//      formatter.setAllowsInvalid(false);
//      DecimalFormat decformat = (DecimalFormat) NumberFormat.getInstance();
//      decformat.applyPattern("##0");
//      decformat.setMaximumIntegerDigits(3);
//      decformat.setMaximumFractionDigits(0);
//      formatter.setFormat(decformat);
//      setFormatter(formatter);
//   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      IntEO eo = (IntEO) value;

      try
      {
         eo.parseValue(getText());
         return 0;
      }
      catch (NumberFormatException ex)
      {
         eo.fireValidationException(ex.getMessage());
         return 1;
      }
   }

   public void passivate() { }

}
