package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.BigDecimalEO;
import com.u2d.view.ActionNotifier;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.math.BigDecimal;

/**
 * Date: Jun 8, 2005
 * Time: 2:32:15 PM
 *
 * @author Eitan Suez
 */
public class BigDecimalEditor extends JFormattedTextField 
                         implements AtomicEditor, ActionNotifier
{
   private boolean _formatSet = false;

   public BigDecimalEditor()
   {
      setColumns(6);
      setHorizontalAlignment(JTextField.RIGHT);

      addFocusListener(new FocusListener()
      {
         public void focusGained(FocusEvent evt) { if (isEditable()) selectAll(); }
         public void focusLost(FocusEvent evt) { }
      });
   }

   private void setupFormatter()
   {
      DecimalFormat decformat = (DecimalFormat) NumberFormat.getInstance();
      decformat.applyPattern("##0.00");

      NumberFormatter formatter = new NumberFormatter(decformat);
      formatter.setAllowsInvalid(false);
      formatter.setValueClass(BigDecimal.class);
      DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(formatter, formatter, formatter);
      setFormatterFactory(formatterFactory);
   }

   public void render(AtomicEObject value)
   {
      BigDecimalEO eo = (BigDecimalEO) value;
      if (!_formatSet)
      {
         setupFormatter();
         _formatSet = true;
      }
      setValue(eo.getValue());
   }

   public int bind(AtomicEObject value)
   {
      BigDecimalEO eo = (BigDecimalEO) value;
      try
      {
         commitEdit();
         BigDecimal readValue = (BigDecimal) getValue();
         eo.setValue(readValue);
         return 0;
      }
      catch (java.text.ParseException ex)
      {
         eo.fireValidationException(ex.getMessage());
         return 1;
      }
      catch (NumberFormatException ex)
      {
         eo.fireValidationException(ex.getMessage());
         return 1;
      }
   }

   public void passivate() { }
}
