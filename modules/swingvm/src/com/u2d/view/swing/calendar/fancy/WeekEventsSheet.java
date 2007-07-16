package com.u2d.view.swing.calendar.fancy;

import com.u2d.calendar.DateTimeBounds;
import com.u2d.calendar.Schedule;

/**
 * @author Eitan Suez
 */
public class WeekEventsSheet
      extends EventsSheet
{
   public WeekEventsSheet(TimeSheet timesheet, DateTimeBounds bounds)
   {
      super(new WeekView(timesheet, bounds));
   }


   public synchronized void addSchedule(Schedule schedule)
   {
      ((WeekView) _view).addSchedule(schedule);
      super.addSchedule(schedule);
   }

   public void removeSchedule(Schedule schedule)
   {
      ((WeekView) _view).removeSchedule(schedule);
      super.removeSchedule(schedule);
   }

   public void clearSchedules()
   {
      ((WeekView) _view).removeSchedules();
      super.clearSchedules();
   }

}
