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
import java.util.Date;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import com.holub.ui.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Eitan Suez
 */
public class DateEditor extends JPanel
                        implements AtomicEditor, ActionNotifier
{
   private JTextField _tf;
   private JButton _calendarBtn;

   private Date_selector_dialog _chooser;
   private Date_selector_panel _date_selector_panel;
   protected CellConstraints _cc;

   private static Icon CAL_ICON, CAL_ROLLOVER_ICON;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL calIconURL = loader.getResource("images/calendar.png");
      CAL_ICON = new ImageIcon(calIconURL);
      calIconURL = loader.getResource("images/calendar_rollover.png");
      CAL_ROLLOVER_ICON = new ImageIcon(calIconURL);
   }

   public DateEditor()
   {
      _tf = new JTextField(9);
      _tf.setHorizontalAlignment(JTextField.RIGHT);

      _calendarBtn = new IconButton(CAL_ICON, CAL_ROLLOVER_ICON);
      _calendarBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            if (_chooser == null) setupChooser();

            if (_tf.getText().trim().length() > 0)
            {
               try
               {
                  DateEO eo = new DateEO();
                  eo.parseValue(_tf.getText());
                  _date_selector_panel.setTime(eo.dateValue());
               }
               catch (java.text.ParseException ex) {}
            }

            Date date = _chooser.select();
            if (date != null)
            {
               DateEO eo = new DateEO();
               eo.setValue(date);
               _tf.setText(_format.format(eo.dateValue()));
            }
         }
      });

      _date_selector_panel = new Date_selector_panel();

      FormLayout layout = new FormLayout("pref, 3px, pref, 3px, pref", "pref");
      setLayout(layout);
      _cc = new CellConstraints();

      KeyListener keyListener  = new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            DateEO deo = new DateEO();
            bind(deo);
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

      add(_tf, _cc.xy(1, 1));
      add(_calendarBtn, _cc.xy(3, 1));

   }


   private void setupChooser()
   {
      Container container = getTopLevelAncestor();
      if (container instanceof Frame)
      {
         _chooser = new Date_selector_dialog((Frame) container, 
                     new Navigable_date_selector(_date_selector_panel));
      }
      else if (container instanceof Dialog)
      {
         _chooser = new Date_selector_dialog((Dialog) container, 
                  new Navigable_date_selector(_date_selector_panel));
      }
      else 
      {
         System.err.println("What's DateField's top-level container?:\n\t"+container);
      }
      _chooser.setDragable(false);
      _chooser.setLocation(positionChooser());
   }
   
   private Point positionChooser()
   {
      // need btn location on screen, not relative,
      // because dialog positioning is screen-relative..
      Point iconLoc = _calendarBtn.getLocationOnScreen();
      return new Point( iconLoc.x + _calendarBtn.getWidth() + 5, iconLoc.y );
   }


   private SimpleDateFormat _format = null;
   public void render(AtomicEObject value)
   {
      _tf.setText(value.toString());
      if (_format == null)
      {
         DateEO eo = (DateEO) value;
         _format = eo.formatter();
         String tooltip = "[" + _format.toPattern() + "]";
         _tf.setToolTipText(tooltip);
      }
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
   
   public synchronized void addFocusListener(FocusListener l)
   {
      _tf.addFocusListener(l);
   }

   public synchronized void removeFocusListener(FocusListener l)
   {
      _tf.removeFocusListener(l);
   }


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
