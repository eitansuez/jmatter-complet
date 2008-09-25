/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * @author Eitan Suez
 */
public class DateTimeEditor extends JPanel implements AtomicEditor, CompositeEditor
{
   private JTextField _tf;

   // TODO:  develop method to input/specify date + time
   private static final String INPUT_TIP = "[mm/dd/yyyy or mm/dd/yy]";

   public DateTimeEditor()
   {
      setLayout(new MigLayout());

      _tf = new JTextField();
      _tf.setColumns(12);
      _tf.setToolTipText(INPUT_TIP);
      _tf.setHorizontalAlignment(JTextField.RIGHT);
      add(_tf);
   }


   public void render(AtomicEObject value)
   {
      _tf.setText(value.toString());
   }
   
   public int bind(AtomicEObject value)
   {
      try
      {
         value.parseValue(_tf.getText());
         return 0;
      }
      catch (java.text.ParseException ex)
      {
         value.fireValidationException(ex.getMessage());
         return 1;
      }
   }

   public void passivate() { }

   public JComponent getEditorComponent() { return _tf; }
}
