/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.ui.IconButton;
import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.DateEO;
import com.u2d.type.atom.TimeInterval;
import com.u2d.view.ActionNotifier;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXMonthView;

/**
 * A version of date editor that uses swingx's monthview for the picker popup.
 * Unlike JXDatePicker, the logic for date entry in the text field is preserved as it was originally
 * 
 * @author Eitan Suez
 */
public class DateEditor3 extends JPanel
                        implements AtomicEditor, ActionNotifier, CompositeEditor
{
   private JTextField _tf;
   private JButton _calendarBtn;

   private JXMonthView _chooser;
   private JPopupMenu _popup;

   private static Icon CAL_ICON, CAL_ROLLOVER_ICON;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL calIconURL = loader.getResource("images/calendar.png");
      CAL_ICON = new ImageIcon(calIconURL);
      calIconURL = loader.getResource("images/calendar_rollover.png");
      CAL_ROLLOVER_ICON = new ImageIcon(calIconURL);
   }

   public DateEditor3()
   {
      _tf = new JTextField(9);
      _tf.setHorizontalAlignment(JTextField.RIGHT);

      _calendarBtn = new IconButton(CAL_ICON, CAL_ROLLOVER_ICON);


      JComboBox box = new JComboBox();
      Object preventHide = box.getClientProperty("doNotCancelPopup");
      _calendarBtn.putClientProperty("doNotCancelPopup", preventHide);

      _calendarBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            if (_chooser == null) setupChooser();

            if (_popup.isVisible())
            {
                _popup.setVisible(false);
               return;
            }

            if (_tf.getText().trim().length() > 0)
            {
               try
               {
                  DateEO eo = new DateEO();
                  eo.parseValue(_tf.getText());
                  _chooser.setSelectionDate(eo.dateValue());
               }
               catch (java.text.ParseException ex) {}
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   Point pt = popupPosition();
                    _popup.show(_calendarBtn, pt.x, pt.y);
                }
            });
         }
      });

      MigLayout layout = new MigLayout("insets 0, alignx right");  // not trailing.
      setLayout(layout);

      KeyListener keyListener  = new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            DateEO deo = new DateEO();
            bind(deo);
            if (deo.isEmpty()) return;
            if (e.getKeyChar() == '+' || e.getKeyChar() == '=')
            {
               deo.add(TimeInterval.ONEDAY);
               render(deo);
               e.consume();
            }
            else if (e.getKeyChar() == '-')
            {
               deo.subtract(TimeInterval.ONEDAY);
               render(deo);
               e.consume();
            }
         }
      };
      _tf.addKeyListener(keyListener);

      add(_tf);
      add(_calendarBtn);

   }


   private void setupChooser()
   {
      _chooser = new JXMonthView();
      _chooser.setTraversable(true);
      _chooser.setDaysOfTheWeek(DateView3.daysOfTheWeek);

      _chooser.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            Date date = _chooser.getSelectionDate();
            if (date != null)
            {
               DateEO eo = new DateEO();
               eo.setValue(date);
               _tf.setText(_format.format(eo.dateValue()));
               _popup.setVisible(false);
               try {
                  _value.parseValue(_tf.getText());
               } catch (ParseException e1) { /* ignore */ }
            }
         }
      });

      _popup = new JPopupMenu();
      _popup.setLayout(new BorderLayout());
      _popup.add(_chooser, BorderLayout.CENTER);
   }

   private Point popupPosition()
   {
      int pad = 5;
      int xright = _calendarBtn.getWidth() + pad;
      int xleft = 0 - pad - _popup.getWidth();
      int ydown = 0;
      int yup = _calendarBtn.getHeight() - _popup.getHeight();

      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      Point iconLoc = _calendarBtn.getLocationOnScreen();
      boolean right = _popup.getWidth() < (screen.width - (iconLoc.x + xright));
      boolean down = _popup.getHeight() < (screen.height - iconLoc.y);

      int x = (right) ? xright : xleft;
      int y = (down) ? ydown : yup;

      return new Point(x, y);
   }


   private SimpleDateFormat _format = null;
   private AtomicEObject _value = null;
   public void render(AtomicEObject value)
   {
      if (value.isEmpty())
      {
         _tf.setText("");
      }
      else
      {
         _tf.setText(value.marshal());
      }
      if (_format == null)
      {
         DateEO eo = (DateEO) value;
         _format = eo.formatter();
         String tooltip = "[" + _format.toPattern() + "]";
         _tf.setToolTipText(tooltip);
      }
      if (_value == null) _value = value;
   }

   public int bind(AtomicEObject value)
   {
      try
      {
         _value = value;
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

   public JComponent getEditorComponent() { return _tf; }

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