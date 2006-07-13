/*
 * Created on Jan 27, 2004
 */
package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;
import com.u2d.domain.*;
import com.u2d.type.composite.*;
import com.u2d.view.*;
import com.u2d.view.swing.list.CommandsButtonView;

/**
 * @author Eitan Suez
 */
public class ViewTester
{

   public ViewTester()
   {
      JFrame f = new JFrame("View Test");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel j = (JPanel) f.getContentPane();

      Shipment shipment = new Shipment("My Shipment", 25);
      shipment.getFrom().setValue(new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749"));
      shipment.getTo().setValue(new USAddress("7500 Ashkelon Rd", "Boston", "MA", "01234"));

      EView iconView = shipment.getIconView();
      EView listItemView = shipment.getListItemView();
      EView formView = shipment.getFormView();

      JPanel p = new JPanel();
      p.add((JComponent) iconView);
      p.add((JComponent) listItemView);
      j.add(p, BorderLayout.NORTH);

      JScrollPane scrollPane = new JScrollPane((JComponent) formView);
      j.add(scrollPane, BorderLayout.CENTER);
      CommandsButtonView cmdsView = new CommandsButtonView();
      cmdsView.bind(shipment, (JComponent) formView, BorderLayout.SOUTH,  formView);
      j.add(cmdsView);
      CommandsButtonView cmdsView2 = new CommandsButtonView();
      cmdsView2.bind(shipment, (JComponent) formView, BorderLayout.EAST,  formView);
      j.add(cmdsView2);

      f.setLocation(300,200);
      f.pack();
      f.setVisible(true);
   }

   public static void main(String[] args)
   {
      new ViewTester();
   }


}
