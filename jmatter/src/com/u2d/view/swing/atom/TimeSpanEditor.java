/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * @author Eitan Suez
 */
public class TimeSpanEditor extends JPanel implements AtomicEditor
{
   private TimeSpinnerEditor _fromEditor, _toEditor;
   private AtomicEditor _dateEditor;
   private long _interval;

   public TimeSpanEditor()
   {
      _dateEditor = new DateEditor();
      _fromEditor = new TimeSpinnerEditor();
      _toEditor = new TimeSpinnerEditor();

      ((JComponent) _dateEditor).setOpaque(false);
      ((JComponent) _fromEditor).setOpaque(false);
      ((JComponent) _toEditor).setOpaque(false);
      
      // the implementation of a feature that ensures that
      // as the from time is changed, the to time moves with
      // it accordingly.  e.g. 8-9 am.  if increase from time
      // from 8 am to 8:30 am, then to time changes to 9:30 am
      // keeping the interval fixed.
      _fromEditor.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            long milis = ((Date) _fromEditor.getValue()).getTime()
                  + _interval;
            _toEditor.setValue(new Date(milis));
         }
      });
      _toEditor.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            //updateInterval :
            _interval = ((Date) _toEditor.getValue()).getTime()
                  - ((Date) _fromEditor.getValue()).getTime();
         }
      });

      FormLayout layout = new FormLayout(
            "right:pref, 5px, left:pref, 5px, right:pref, 5px , left:pref",
            "pref, 5px, pref");
      DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
      CellConstraints cc = new CellConstraints();

      builder.addLabel("Date:", cc.xy(1, 1));
      builder.add((JComponent) _dateEditor, cc.xyw(3, 1, 5));
      builder.addLabel("From:", cc.xy(1, 3));
      builder.add((JComponent) _fromEditor, cc.xy(3, 3));
      builder.addLabel("To:", cc.xy(5, 3));
      builder.add((JComponent) _toEditor, cc.xy(7, 3));
   }

   public void render(AtomicEObject value)
   {
      TimeSpan span = (TimeSpan) value;
      _interval = span.duration().getMilis();
      
      Date startDate = span.startDate();
      _dateEditor.render(new DateEO(startDate));
      TimeEO from = new TimeEO(startDate.getTime());
      _fromEditor.render(from);
      TimeEO to = new TimeEO(span.endDate().getTime());
      _toEditor.render(to);
   }

   public int bind(AtomicEObject value)
   {
      DateEO date_eo = new DateEO();
      TimeEO from_eo = new TimeEO();
      TimeEO to_eo = new TimeEO();

      int errors = 0;
      errors += _dateEditor.bind(date_eo);
      errors += _fromEditor.bind(from_eo);
      errors += _toEditor.bind(to_eo);
      
      if (errors > 0 || date_eo.isEmpty())
      {
         value.fireValidationException("Unable to parse time span");
         return 1;
      }

      Date fromDate = from_eo.dateValue(date_eo.dateValue());
      Date toDate = to_eo.dateValue(date_eo.dateValue());
      TimeSpan span = new TimeSpan(fromDate, toDate);
      
      value.setValue(span);
      return 0;
   }

   public void passivate()
   {
   }

}
