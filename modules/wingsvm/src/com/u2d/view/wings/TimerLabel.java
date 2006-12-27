package com.u2d.view.wings;

import org.wings.SLabel;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 22, 2006
 * Time: 11:47:04 PM
 */
public class TimerLabel extends SLabel implements java.awt.event.ActionListener
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
