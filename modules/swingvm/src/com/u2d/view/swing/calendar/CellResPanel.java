/*
 * Created on Apr 13, 2004
 */
package com.u2d.view.swing.calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.u2d.type.atom.*;
import com.u2d.calendar.CellResChoice;

import java.util.*;

/**
 * @author Eitan Suez
 */
class CellResPanel
      extends JPanel implements ActionListener
{
   private JComboBox _combo = new JComboBox();
   private ICalView _weekCal;
   
   CellResPanel(ICalView weekCal, CellResChoice initVal)
   {
      _weekCal = weekCal;
      
      setLayout(new FlowLayout(FlowLayout.LEFT));
      
      JLabel label = new JLabel("Increments of:");
      add(label);
      
      for (CellResChoice choice : CellResChoice.values())
      {
         _combo.addItem(choice);
      }
      
      _combo.setSelectedItem(initVal);
      
      _combo.addActionListener(this);
      add(_combo);
      label.setLabelFor(_combo);
   }
   
   public void actionPerformed(ActionEvent evt)
   {
      CellResChoice item = (CellResChoice) _combo.getSelectedItem();
      _weekCal.setCellResolution(new TimeInterval(Calendar.MINUTE, item.minutes()));
   }

}

