package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.view.ActionNotifier;
import com.u2d.type.atom.FloatEO;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.wings.SFormattedTextField;
import org.wings.SConstants;
import org.wings.text.SNumberFormatter;

/**
 * Date: Jun 8, 2005
 * Time: 2:32:15 PM
 *
 * @author Eitan Suez
 */
public class FloatEditor extends SFormattedTextField
                         implements AtomicEditor, ActionNotifier
{
   public FloatEditor()
   {
      setFormatting();
      setColumns(6);
      setHorizontalAlignment(SConstants.RIGHT);

//      addFocusListener(new FocusListener()
//      {
//         public void focusGained(FocusEvent evt) { if (isEditable()) selectAll(); }
//         public void focusLost(FocusEvent evt) { }
//      });
   }

   private void setFormatting()
   {
      SNumberFormatter formatter = new SNumberFormatter();
//      formatter.setAllowsInvalid(false);
      DecimalFormat decformat = (DecimalFormat) NumberFormat.getInstance();
      decformat.applyPattern("##0.00");
      decformat.setMaximumIntegerDigits(3);
      decformat.setMaximumFractionDigits(2);
      formatter.setNumberFormat(decformat);
      setFormatter(formatter);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      FloatEO eo = (FloatEO) value;
      try
      {
         eo.parseValue(getText());
         return 0;
      }
      catch (NumberFormatException ex)
      {
         // this doesn't work for obvious reasons
         eo.fireValidationException("Invalid value for a numeric field");
         return 1;
      }
   }

   public void passivate() { }
}
