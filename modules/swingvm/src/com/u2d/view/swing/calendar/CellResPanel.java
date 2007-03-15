/*
 * Created on Apr 13, 2004
 */
package com.u2d.view.swing.calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import com.u2d.calendar.CellResChoice;
import com.u2d.view.swing.atom.ChoiceEOModel;

/**
 * @author Eitan Suez
 */
class CellResPanel extends JPanel implements ActionListener, PropertyChangeListener
{
   private JComboBox _combo = new JComboBox();
   private ChoiceEOModel _weekmodel, _daymodel;
   private TimeSheet _timesheet;
   private TimeIntervalView _currentView;
   
   CellResPanel(TimeSheet timesheet)
   {
      _timesheet = timesheet;

      setLayout(new FlowLayout(FlowLayout.LEFT));
      
      JLabel label = new JLabel("Increments of:");
      add(label);
      add(_combo);
      label.setLabelFor(_combo);
      
      _combo.addActionListener(this);
      _weekmodel = new ChoiceEOModel(_timesheet.getWeekView().getCellResolution());
      _daymodel = new ChoiceEOModel(_timesheet.getDayView().getCellResolution());
      
      bindTo(_timesheet.getWeekView());
   }
   
   public void bindTo(TimeIntervalView view)
   {
      if (_currentView != null)
         _currentView.removePropertyChangeListener("cellResolution", this);
      view.addPropertyChangeListener("cellResolution", this);
      _currentView = view;
      
      _combo.setModel(view instanceof WeekView ? _weekmodel : _daymodel);
   }
   
   public void actionPerformed(ActionEvent e)
   {
      _timesheet.setCellResolution((CellResChoice) _combo.getModel().getSelectedItem());
   }
   public void propertyChange(PropertyChangeEvent evt)
   {
      _combo.getModel().setSelectedItem(evt.getNewValue());
   }
}

