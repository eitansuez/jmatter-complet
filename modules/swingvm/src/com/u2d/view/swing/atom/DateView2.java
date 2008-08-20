/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.view.*;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.atom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import com.holub.ui.*;
import java.util.Date;

/**
 * A date editor where the month navigator is always visible (as opposed
 * to DateEditor where the month miniview acts as a popup picker).  One
 * goal is to use this view to drive a flexible date navigator in the
 * calendar view.
 *  
 * @author Eitan Suez
 */
public class DateView2 extends JPanel implements AtomicEView, Editor
{
   private DateEO _eo;
   private JTextField _tf;
//   private final JLabel _tip = new JLabel(INPUT_TIP);
//   private final static String INPUT_TIP = "[mm/dd/yyyy or mm/dd/yy]";

   private Date_selector_panel _date_selector_panel;
   private transient CommandsContextMenuView _cmdsView;

   
   public DateView2(DateEO eo)
   {
      setOpaque(false);
      
      _eo = eo;
      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_eo, this);
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
                        _date_selector_panel.setTime(_eo.dateValue());
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
      
      _date_selector_panel = new Date_selector_panel();
      Navigable_date_selector miniview = new Navigable_date_selector(
            new Titled_date_selector(_date_selector_panel));
      
      miniview.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  Date date = _date_selector_panel.get_selected_date();
                  if (date == null) return;
                  _eo.setValue(date);
               }
            });
      
      miniview.setBorder(BorderFactory.createLineBorder(Color.black));
      add(miniview);

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
//      _tip.setVisible(editable);
   }
   public boolean isEditable() { return _tf.isEditable(); }
   
   public void detach()
   {
      _eo.removeChangeListener(this);
      _cmdsView.detach();
      if (_eo.parentObject() != null)
         _eo.parentObject().removeChangeListener(this);
   }
   
   public Dimension getMinimumSize() { return getPreferredSize(); }
   
}
