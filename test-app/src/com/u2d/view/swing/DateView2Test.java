/*
 * Created on Nov 19, 2004
 */
package com.u2d.view.swing;

import com.u2d.type.atom.*;
import com.u2d.view.swing.atom.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Date;

/**
 * @author Eitan Suez
 */
public class DateView2Test
{
   JLabel _thedate;
   DateEO _eo;
   
   public DateView2Test()
   {
      JFrame f = new JFrame("DateView2Test");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocation(100,100);
      
      JPanel p = (JPanel) f.getContentPane();
      p.setLayout(new BorderLayout());
      _thedate = new JLabel();
      p.add(_thedate, BorderLayout.LINE_START);
      
      Date now = new Date();
      _eo = new DateEO(now);
      
      DateView2 dv2 = new DateView2(_eo);
      _eo.addChangeListener(new ChangeListener()
            {
         public void stateChanged(ChangeEvent evt)
         {
            _thedate.setText(_eo.toString());
         }
            });
      
      p.add(dv2, BorderLayout.LINE_END);
      
      f.pack();
      f.setVisible(true);
   }

   
   public static void main(String[] args)
   {
      new DateView2Test();
   }
}
