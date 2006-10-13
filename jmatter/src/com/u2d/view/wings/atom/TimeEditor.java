package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import org.wings.STextField;
import org.wings.SConstants;

/**
 * @author Eitan Suez
 */
public class TimeEditor
      extends STextField
      implements AtomicEditor
{
   private final String INPUT_TIP = "[hh:mm]";

   public TimeEditor()
   {
      setColumns(6);
      setToolTipText(INPUT_TIP);
      setHorizontalAlignment(SConstants.RIGHT_ALIGN);
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
