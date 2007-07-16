/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.event.ActionEvent;
import java.util.Date;
import com.u2d.calendar.*;

/**
 * @author Eitan Suez
 */
public class CalActionEvent extends ActionEvent
{
   protected Date _time;
   protected Schedulable _schedulable;
   
   public CalActionEvent(Object source, Date time)
   {
      super(source, ActionEvent.ACTION_PERFORMED, "");
      _time = time;
   }

   public CalActionEvent(Object source, Date time, Schedulable schedulable)
   {
      this(source, time);
      _schedulable = schedulable;
   }
   
   public Date getTime() { return _time; }
   public Schedulable getSchedulable() { return _schedulable; }
   
}
