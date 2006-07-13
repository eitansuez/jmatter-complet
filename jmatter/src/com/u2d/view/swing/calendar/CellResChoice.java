/*
 * Created on Apr 13, 2004
 */
package com.u2d.view.swing.calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.u2d.type.atom.*;
import java.util.*;

/**
 * @author Eitan Suez
 */
class CellResChoice extends JPanel implements ActionListener
{
   private JComboBox _combo = new JComboBox();
   private ICalView _weekCal;
   
   CellResChoice(ICalView weekCal)
   {
      _weekCal = weekCal;
      
      setLayout(new FlowLayout(FlowLayout.LEFT));
      
      JLabel label = new JLabel("Increments of:");
      add(label);
      
      _combo.addItem(new Pair("15 min", 15));
      _combo.addItem(new Pair("30 min", 30));
      _combo.addItem(new Pair("1 hr", 60));
      _combo.setSelectedIndex(1);
      
      _combo.addActionListener(this);
      add(_combo);
      label.setLabelFor(_combo);
   }
   
   public void actionPerformed(ActionEvent evt)
   {
      Pair item = (Pair) _combo.getSelectedItem();
      _weekCal.setCellResolution(new TimeInterval(Calendar.MINUTE, item.data));
   }

   class Pair
   {
      String caption;  int data;
      Pair(String caption, int data)
      {
         this.caption = caption;
         this.data = data;
      }
      public String toString() { return caption; }
   }
}

