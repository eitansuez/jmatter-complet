/*
 * Created on Nov 24, 2004
 */
package com.u2d.view.swing.calendar.fancy;

import com.u2d.calendar.Schedule;
import com.u2d.calendar.DateTimeBounds;

/**
 * @author Eitan Suez
 */
public class DayEventsSheet
      extends EventsSheet
{
   public DayEventsSheet(TimeSheet timesheet, DateTimeBounds bounds)
   {
      super(new DayView(timesheet, bounds));
   }


   public synchronized void addSchedule(Schedule schedule)
   {
      ((DayView) _view).addSchedule(schedule);
      super.addSchedule(schedule);
   }

   public void removeSchedule(Schedule schedule)
   {
      ((DayView) _view).removeSchedule(schedule);
      super.removeSchedule(schedule);
   }

   public void clearSchedules()
   {
      ((DayView) _view).removeSchedules();
      super.clearSchedules();
   }

   public void setScheduleVisible(Schedule schedule, boolean visible)
   {
      super.setScheduleVisible(schedule, visible);
      ((DayView) _view).setScheduleVisible(schedule, visible);
   }
}
