package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import org.wings.SPanel;
import org.wings.STextField;
import org.wings.SConstants;
import org.wings.SGridLayout;

/**
 * @author Eitan Suez
 */
public class DateTimeEditor extends SPanel implements AtomicEditor
{
   private STextField _tf;

   // TODO:  develop method to input/specify date + time
   private static final String INPUT_TIP = "[mm/dd/yyyy or mm/dd/yy]";

   public DateTimeEditor()
   {
      setLayout(new SGridLayout(1, 2));

      _tf = new STextField();
      _tf.setColumns(12);
      _tf.setToolTipText(com.u2d.view.wings.atom.DateTimeEditor.INPUT_TIP);
      _tf.setHorizontalAlignment(SConstants.RIGHT);
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

}
