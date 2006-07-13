/*
 * Created on Apr 15, 2005
 */
package com.u2d.basic;

import javax.swing.*;
import javax.swing.border.LineBorder;
import com.u2d.ui.LineBorderEnh;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class RoundRect extends JLabel
{
   public RoundRect(Color color)
   {
      super();
      //setBorder(BorderFactory.createLineBorder(color));
      LineBorder roundedBorder = new LineBorderEnh(color, 2, 30);
      setBorder(roundedBorder);
      setText("Hello World");
   }

   public static void main(String[] args)
   {
      JFrame f = new JFrame();
      JPanel p = (JPanel) f.getContentPane();
      
      p.setLayout(new BorderLayout());
      
      RoundRect r = new RoundRect(Color.blue);
      
      p.add(r, BorderLayout.CENTER);
      
      
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,400,400);
      f.setVisible(true);
   }
}
