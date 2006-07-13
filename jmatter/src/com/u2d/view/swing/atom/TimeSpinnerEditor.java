package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.TimeEO;
import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Date: Jun 7, 2005
 * Time: 6:02:52 PM
 *
 * @author Eitan Suez
 */
public class TimeSpinnerEditor extends JSpinner implements AtomicEditor
{
   public TimeSpinnerEditor()
   {
      SpinnerModel model =
            new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
      setModel(model);
      String pattern = TimeEO.DISPLAY_FORMAT.toPattern();
      JComponent editor = new JSpinner.DateEditor(this, pattern);
      setEditor(editor);
   }

   public void render(AtomicEObject value)
   {
      TimeEO eo = (TimeEO) value;
      /*spinner.*/setValue(eo.dateValue());
   }

   public int bind(AtomicEObject value)
   {
      TimeEO eo = (TimeEO) value;
      eo.setValue((Date) /*spinner.*/getValue());
      return 0;
   }

   public void passivate() { }

}
