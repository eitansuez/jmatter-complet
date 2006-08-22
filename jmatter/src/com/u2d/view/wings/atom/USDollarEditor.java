package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import org.wings.STextField;
import org.wings.SConstants;

/**
 * Date: Jun 8, 2005
 * Time: 2:54:17 PM
 *
 * @author Eitan Suez
 */
public class USDollarEditor extends STextField implements AtomicEditor
{
   public USDollarEditor()
   {
      setColumns(6);
      setHorizontalAlignment(SConstants.RIGHT);
//      UIUtils.selectOnFocus(this);
   }

   public void render(AtomicEObject value)
   {
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
