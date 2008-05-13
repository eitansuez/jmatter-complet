package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.FloatEO;
import com.u2d.view.ActionNotifier;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

/**
 * Date: Jun 8, 2005
 * Time: 2:32:15 PM
 *
 * @author Eitan Suez
 */
public class FloatEditor extends JFormattedTextField 
                         implements AtomicEditor, ActionNotifier, ValidationListener
{
   private boolean _formatSet = false;
   
   public FloatEditor()
   {
      setColumns(6);
      setHorizontalAlignment(JTextField.RIGHT);

      addFocusListener(new FocusAdapter()
      {
         public void focusGained(FocusEvent evt)
         {
            if (isEditable())
            {
               SwingUtilities.invokeLater(new Runnable() { public void run() {
                  selectAll();
               } });
            }

         }
      });
   }

   private void setupFormatter(FloatEO eo)
   {
      NumberFormatter formatter = new NumberFormatter(eo.format());
      formatter.setAllowsInvalid(false);
      formatter.setValueClass(Double.class);
      DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(formatter, formatter, formatter);
      setFormatterFactory(formatterFactory);
   }

   public void render(AtomicEObject value)
   {
      FloatEO eo = (FloatEO) value;
      if (!_formatSet)
      {
         setupFormatter(eo);
         _formatSet = true;
      }
      setValue(eo.doubleValue());
   }

   public int bind(AtomicEObject value)
   {
      FloatEO eo = (FloatEO) value;
      try
      {
         commitEdit();
         double readValue = (Double) getValue();
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

   public void validationException(ValidationEvent evt)
   {
      AtomicView.decorateBackground(this, evt);
   }

   public void passivate() { }
}
