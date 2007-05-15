package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.ui.UIUtils;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:54:17 PM
 *
 * @author Eitan Suez
 */
public class USDollarEditor extends JTextField implements AtomicEditor
{
   public USDollarEditor()
   {
      setColumns(6);
      setHorizontalAlignment(JTextField.RIGHT);
      UIUtils.selectOnFocus(this);
   }

   public void render(AtomicEObject value)
   {
      if (value.field() != null && value.field().displaysize() > 0
            && value.field().displaysize() != getColumns())
      {
         setColumns(value.field().displaysize());
      }

      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      try
      {
         String text = getText().trim();
         if (!text.startsWith("$"))
            text = "$" + text;
         value.parseValue(text);
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
