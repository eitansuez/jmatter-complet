package com.u2d.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 14, 2007
 * Time: 1:34:34 PM
 */
public class MarginTests extends JPanel
{
   static Border emptyBorder = new EmptyBorder(0, 0, 0,0);
   static Border lineBorder = new LineBorder(Color.black,  1);
   
   JLabel label;
   
   public MarginTests()
   {
      FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
      setLayout(layout);
      
      label = new JLabel("Hello World");
      label.setOpaque(true);
      label.setBackground(Color.white);
      label.setBorder(lineBorder);
      add(label);
   }
   public void printDiagnostics()
   {
      System.out.println("label size: "+label.getSize());
      System.out.println("panel size: "+getSize());
   }
   public static JFrame frame(JComponent contents)
   {
      JFrame f = new JFrame();
      f.setContentPane(contents);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocation(100,100);
      f.pack();
      return f;
   }

   public static void main(String[] args) throws Exception
   {
      MarginTests panel = new MarginTests();
      frame(panel).setVisible(true);
      panel.printDiagnostics();
   }
}
