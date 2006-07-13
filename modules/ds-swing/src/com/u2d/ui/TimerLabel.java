/*
 * Created on Feb 26, 2004
 */
package com.u2d.ui;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * @author Eitan Suez
 */
public class TimerLabel extends JLabel implements java.awt.event.ActionListener
{
   private Timer _timer;
   
   public TimerLabel(int delay_ms)
   {
      super(" ");
      _timer = new Timer(delay_ms, this);
   }
   
   public void setText(String text)
   {
      super.setText(text);
      if (_timer != null)
         _timer.restart();
   }
   
   public void actionPerformed(java.awt.event.ActionEvent evt)
   {
      setText(" ");
   }
}
