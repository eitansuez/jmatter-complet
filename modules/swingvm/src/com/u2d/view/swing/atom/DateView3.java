/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.view.*;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.atom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import org.jdesktop.swingx.JXMonthView;

/**
 * A version of DateView2 that uses the SwingX JXMonthView instead of
 * date month view that I had originally coded (which was adapted from
 * a holub article).
 *
 * @author Eitan Suez
 */
public class DateView3 extends JPanel implements AtomicEView, Editor
{
   private DateEO _eo;
   private JTextField _tf;

   public static String[] daysOfTheWeek = {"S", "M", "T", "W", "T", "F", "S"};

   private JXMonthView _monthView;

   public DateView3(DateEO eo)
   {
      setOpaque(false);

      _eo = eo;
      if (_eo.parentObject() != null)
         _eo.parentObject().addChangeListener(this);
      _eo.addChangeListener(this);

      _tf = new JTextField();
      _tf.setColumns(9);
      _tf.setHorizontalAlignment(JTextField.RIGHT);
      _tf.setText(_eo.toString());

      _tf.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  if (_tf.getText().trim().length() > 0)
                  {
                     try
                     {
                        _eo.parseValue(_tf.getText());
                        _monthView.setSelectionDate(_eo.dateValue());
                     }
                     catch (java.text.ParseException ex) {}
                  }
               }
            });

      JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
      topPnl.setOpaque(false);
      topPnl.add(_tf);
//      topPnl.add(_tip);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(topPnl);

      _monthView = new JXMonthView();
      _monthView.setTraversable(true);
      _monthView.setDaysOfTheWeek(daysOfTheWeek);

      _monthView.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  Date date = _monthView.getSelectionDate();
                  if (date == null) return;
                  _eo.setValue(date);
               }
            });

      _monthView.setBorder(BorderFactory.createLineBorder(Color.black));
      add(_monthView);

      setEditable(true);
      stateChanged(null);
   }


   public void stateChanged(ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _tf.setText(_eo.toString());
         }
      });
   }

   public int transferValue()
   {
      try
      {
         _eo.parseValue(_tf.getText());
         return 0;
      }
      catch (java.text.ParseException ex)
      {
         _eo.fireValidationException(ex.getMessage());
         return 1;
      }
   }
   public int validateValue() { return _eo.validate(); }


   public EObject getEObject() { return _eo; }

   public void setEditable(boolean editable)
   {
      _tf.setEditable(editable);
   }
   public boolean isEditable() { return _tf.isEditable(); }

   public void detach()
   {
      _eo.removeChangeListener(this);
      if (_eo.parentObject() != null)
         _eo.parentObject().removeChangeListener(this);
   }

   public Dimension getMinimumSize() { return getPreferredSize(); }

}