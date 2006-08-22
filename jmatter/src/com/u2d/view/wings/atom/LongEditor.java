package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.LongEO;
import org.wings.STextField;
import org.wings.SConstants;

/**
 * Date: Jun 8, 2005
 * Time: 2:23:21 PM
 *
 * @author Eitan Suez
 */
public class LongEditor extends STextField implements AtomicEditor
{
   public LongEditor()
   {
      setColumns(12);
      setHorizontalAlignment(SConstants.RIGHT);
//      UIUtils.selectOnFocus(this);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      LongEO eo = (LongEO) value;

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
