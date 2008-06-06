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
public class CellResPanel extends JPanel implements ActionListener, PropertyChangeListener
{
   private JComboBox _combo = new JComboBox();
   private ChoiceEOModel _model;
   private ITimeSheet _timesheet;
   
   public CellResPanel(ITimeSheet timesheet)
   {
      setOpaque(false);
      
      _timesheet = timesheet;
      _model = new ChoiceEOModel(_timesheet.getCellResolution());
      _combo.setModel(_model);
      _combo.setToolTipText("Alternately use Ctrl+Scrollwheel on day and week views");

      setLayout(new FlowLayout(FlowLayout.LEFT));
      
      JLabel label = new JLabel("Increments of:");
      add(label);
      add(_combo);
      label.setLabelFor(_combo);
      
      _combo.addActionListener(this);
      _timesheet.addPropertyChangeListener("cellResolution", this);
   }
   
   public void actionPerformed(ActionEvent e)
   {
      _timesheet.setCellResolution((CellResChoice) _model.getSelectedItem());
   }
   public void propertyChange(PropertyChangeEvent evt)
   {
      _model.setSelectedItem(evt.getNewValue());
   }
}

