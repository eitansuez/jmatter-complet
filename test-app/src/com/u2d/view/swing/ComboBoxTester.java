/*
 * Created on Mar 31, 2005
 */
package com.u2d.view.swing;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * 
 * The main purpose of this class is to test whether
 * setSelectedItem works on a jcombobox when that combobox
 * is enabled, disabled.  conclusion:  enabled flag does
 * not disturb ability to setselectedindex.  furthermore,
 * setselectedindex is not disturbed by visibility.
 * 
 * @author Eitan Suez
 */
public class ComboBoxTester
{
   JComboBox _c;
   JTextField _tf = new JTextField();
   
   public ComboBoxTester()
   {
      JFrame f = new JFrame("Testing JComboBox..");
      JPanel p = (JPanel) f.getContentPane();
      p.setLayout(new BorderLayout());
      
      JPanel center = new JPanel(new GridLayout(2, 1));
      String[] items = {"one", "two", "three", "four"};
      _c = new JComboBox(items);
      center.add(_c);
      center.add(_tf);
      p.add(center, BorderLayout.CENTER);
      
      JPanel btnPnl = new JPanel();
      btnPnl.add(new SelectorButton());
      btnPnl.add(new EnabledToggleButton());
      btnPnl.add(new VisibilityToggleButton());
      btnPnl.add(new Selector2Button());
      
      p.add(btnPnl, BorderLayout.PAGE_END);
      
      
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocation(100,100);
      f.pack();
      f.setVisible(true);
   }
   
   Random _random = new Random();
   
   class SelectorButton extends JButton
   {
      public SelectorButton()
      {
         super("Select Index (Random)");
         addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  int i = _random.nextInt(_c.getItemCount());
                  _c.setSelectedIndex(i);
               }
            });
      }
   }
   class EnabledToggleButton extends JButton
   {
      public EnabledToggleButton()
      {
         super("Toggle Enabled");
         addActionListener(new ActionListener()
               {
                  public void actionPerformed(ActionEvent evt)
                  {
                     _c.setEnabled(!_c.isEnabled());
                  }
               });
      }
   }
   class VisibilityToggleButton extends JButton
   {
      public VisibilityToggleButton()
      {
         super("Toggle Visibility");
         addActionListener(new ActionListener()
               {
                  public void actionPerformed(ActionEvent evt)
                  {
                     _c.setVisible(!_c.isVisible());
                  }
               });
      }
   }
   class Selector2Button extends JButton
   {
      public Selector2Button()
      {
         super("Select Item (textfield)");
         addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  Object item = _tf.getText();
                  _c.setSelectedItem(item);
               }
            });
      }
   }

   public static void main(String[] args)
   {
      new ComboBoxTester();
   }

}
