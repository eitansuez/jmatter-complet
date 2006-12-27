package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import org.wings.STextField;
import org.wings.SConstants;

/**
 * Date: Jun 8, 2005
 * Time: 2:40:18 PM
 *
 * @author Eitan Suez
 */
public class PercentEditor extends STextField implements AtomicEditor
{
   public PercentEditor()
   {
      setColumns(6);
      setHorizontalAlignment(SConstants.RIGHT);
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
