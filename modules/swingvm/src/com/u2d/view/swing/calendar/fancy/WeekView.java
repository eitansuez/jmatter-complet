/*
 * Created on Sep 17, 2003
 */
package com.u2d.view.swing.calendar.fancy;

import java.util.*;
import com.u2d.calendar.DateTimeBounds;
import com.u2d.calendar.Schedule;
import com.u2d.calendar.Schedulable;
import com.u2d.view.swing.calendar.BaseWeekView;

/**
 * @author Eitan Suez
 */
public class WeekView extends BaseWeekView
{
   public WeekView(TimeSheet timesheet, DateTimeBounds bounds)
   {
      super(timesheet, bounds);
   }

   protected void fireDoubleClickEvent(Calendar cal)
   {
      Schedulable schedulable = null;
      if (_schedules.size() == 1)
      {
         schedulable = _schedules.get(0).getSchedulable();
      }
      fireActionEvent(cal.getTime(), schedulable);
   }

   private java.util.List<Schedule> _schedules = new ArrayList<Schedule>();
   public void addSchedule(Schedule schedule) { _schedules.add(schedule); }
   public void removeSchedule(Schedule schedule) { _schedules.remove(schedule); }
   public void removeSchedules() { _schedules.clear(); }

}