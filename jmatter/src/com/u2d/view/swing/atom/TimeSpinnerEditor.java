package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.TimeEO;
import javax.swing.*;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Date: Jun 7, 2005
 * Time: 6:02:52 PM
 *
 * @author Eitan Suez
 */
public class TimeSpinnerEditor extends JSpinner implements AtomicEditor
{
   boolean _formatterSet = false;
   
   public TimeSpinnerEditor()
   {
      SpinnerModel model =
            new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
      setModel(model);
   }

   public void render(AtomicEObject value)
   {
      TimeEO eo = (TimeEO) value;
      /*spinner.*/setValue(eo.dateValue());
      initFormat(eo.formatter());
   }
   
   private void initFormat(SimpleDateFormat formatter)
   {
      if (!_formatterSet)
      {
         _formatterSet = true;
         JComponent editor = new JSpinner.DateEditor(this, formatter.toPattern());
         setEditor(editor);
      }
   }

   public int bind(AtomicEObject value)
   {
      TimeEO eo = (TimeEO) value;
      eo.setValue((Date) /*spinner.*/getValue());
      return 0;
   }

   public void passivate() { }

}
