/*
 * Created on Apr 28, 2005
 */
package com.u2d.basic;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;

/**
 * @author Eitan Suez
 */
public class PanelColorTest
{
   public static void main(String[] args)
   {
      JFrame f = new JFrame("PanelColorTest");
      JPanel p = (JPanel) f.getContentPane();
      
      p.setLayout(new BorderLayout());
      
      JPanel inner = new JPanel();
      inner.setOpaque(false);
      inner.setBackground(Color.blue);
      
      JScrollPane sp = new JScrollPane(inner);
      p.add(sp, BorderLayout.CENTER);
      
      p.setOpaque(true);
      p.setBackground(Color.green);
      
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,400,400);
      f.setVisible(true);
   }
}
