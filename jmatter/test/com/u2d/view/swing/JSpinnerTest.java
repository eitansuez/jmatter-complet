package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;

/**
 * Date: Jun 7, 2005
 * Time: 5:47:08 PM
 *
 * @author Eitan Suez
 */
public class JSpinnerTest extends JPanel
{

   public JSpinnerTest()
   {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 8);
      cal.set(Calendar.MINUTE, 30);
      Date date = cal.getTime();

      SpinnerModel model = new SpinnerDateModel(date, null, null, Calendar.MINUTE);

      JSpinner spinner = new JSpinner(model);
      JComponent editor = new JSpinner.DateEditor(spinner, "hh:mm a");
      spinner.setEditor(editor);
      
      add(spinner);
   }

   public static void main(String[] args)
   {
      JFrame f = new JFrame("Time Span View Check");
      f.getContentPane().add(new JSpinnerTest(), BorderLayout.CENTER);
      f.setLocation(300,300);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }
}
