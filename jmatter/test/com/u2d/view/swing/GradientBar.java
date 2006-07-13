/*
 * Created on Apr 4, 2005
 */
package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class GradientBar extends JPanel
{
   
   public GradientBar()
   {
      setLayout(new BorderLayout());
      JLabel label = new JLabel("This is a test");
      label.setOpaque(false);
      setOpaque(false);
      add(label, BorderLayout.CENTER);
      setBorder(BorderFactory.createLineBorder(Color.black));
   }
   
   protected void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      Color transparent = new Color(0, 0, 0, 0);
      GradientPaint paint = new GradientPaint(0, 0, Color.red, getWidth(), 0, transparent);
      g2.setPaint(paint);
      g2.fill(getBounds());
      super.paintComponent(g);
   }
   
   
   public static void main(String[] args)
   {
      JFrame f = new JFrame("Testing Gradient Bar");
      JPanel p = (JPanel) f.getContentPane();
      p.add(new GradientBar(), BorderLayout.NORTH);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,400,400);
      f.setVisible(true);
   }

}
