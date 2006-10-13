package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.view.ActionNotifier;
import org.wings.SPanel;
import org.wings.STextField;
import org.wings.SConstants;
import java.awt.event.ActionListener;

/**
 * @author Eitan Suez
 */
public class DateEditor
      extends SPanel
      implements AtomicEditor, ActionNotifier
{
   private STextField _tf;
   private final static String INPUT_TIP = "[mm/dd/yyyy or mm/dd/yy]";

   public DateEditor()
   {
      _tf = new STextField();
      _tf.setColumns(9);
      _tf.setToolTipText(INPUT_TIP);
      _tf.setHorizontalAlignment(SConstants.RIGHT_ALIGN);

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

   // ===
   
   public void passivate() { }

   
   // added specifically for integration with table cell editing
   public void addActionListener(ActionListener al)
   {
      _tf.addActionListener(al);
   }
   public void removeActionListener(ActionListener al)
   {
      _tf.removeActionListener(al);
   }


}
