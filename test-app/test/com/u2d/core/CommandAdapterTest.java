/*
 * Created on Jan 20, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class CommandAdapterTest extends TestCase
{

   public static void main(String[] args)
   {
//      EObject shipment = new Shipment("My Shipment", 5);
      
      JFrame f = new JFrame();
//      JPanel pane = (JPanel) f.getContentPane();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(300,300,400,400);
      
      
      //pane.add(shipment.getDefaultView(), BorderLayout.CENTER);
      f.setVisible(true);
      
      // In View.class:
      //
      // JPanel buttonPanel = new JPanel();
      // for (int i=0; i<commands.length; i++)
      // {
      //    CommandAdapter cmder = new CommandAdapter(commands[i], getEObject());
      //    JButton button = new JButton(cmder);
      //    buttonPanel.add(button);
      // }
      // add(buttonPanel, BorderLayout.PAGE_END);
   }
   
   public void testDummy()
   {
      assertTrue(true);
   }

}
