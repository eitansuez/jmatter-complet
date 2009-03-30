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
import net.miginfocom.swing.MigLayout;

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
      _dateEditor = new DateEditor3();
      _fromEditor = new TimeSpinnerEditor();
      _toEditor = new TimeSpinnerEditor();

      ((JComponent) _dateEditor).setOpaque(false);
      _fromEditor.setOpaque(false);
      _toEditor.setOpaque(false);
      
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

      MigLayout layout = new MigLayout("insets 0, gapy 2", "[trailing][leading][trailing][leading]", "[][]");
      setLayout(layout);

      add(new JLabel("Date:"));
      add((JComponent) _dateEditor, "span, wrap");
      add(new JLabel("From:"));
      add(_fromEditor);
      add(new JLabel("To:"), "gap unrel");
      add(_toEditor);
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

   public void passivate() { }

}
