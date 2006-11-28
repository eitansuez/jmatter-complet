package com.u2d.view.swing;

import com.u2d.type.atom.TimeSpan;
import com.u2d.view.swing.atom.AtomicView;
import com.u2d.model.Editor;
import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 7, 2005
 * Time: 3:44:54 PM
 *
 * @author Eitan Suez
 */
public class TimeSpanViewTest extends JPanel
{
   public TimeSpanViewTest()
   {
      setLayout(new BorderLayout());
      AtomicView view = new AtomicView();
      view.bind(new TimeSpan());
      ((Editor) view).setEditable(true);
      add((JComponent) view, BorderLayout.CENTER);
   }

   public static void main(String[] args)
   {
      JFrame f = new JFrame("Time Span View Check");
      f.getContentPane().add(new TimeSpanViewTest(), BorderLayout.CENTER);
      f.setLocation(300,300);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }
}
