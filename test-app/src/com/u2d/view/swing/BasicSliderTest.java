package com.u2d.view.swing;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 18, 2006
 * Time: 9:41:36 PM
 */
public class BasicSliderTest extends JFrame
{
   public BasicSliderTest()
   {
      JPanel contentPane = (JPanel) getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(scrollbar(), BorderLayout.CENTER);
   }
   
   private JScrollBar scrollbar()
   {
      JScrollBar scrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
      scrollbar.setUnitIncrement(1);
      scrollbar.setBlockIncrement(1);
      scrollbar.setMinimum(1);
      scrollbar.setMaximum(3);
      scrollbar.setValue(1);
      scrollbar.setVisibleAmount(1);
      return scrollbar;
   }

   public static void main(String[] args)
   {
      JFrame f = new BasicSliderTest();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      f.setLocation(100,100);
      f.pack();
      f.setVisible(true);

   }
}
