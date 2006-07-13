/*
 * Created on Nov 24, 2004
 */
package com.u2d.view.swing.calendar;

import com.u2d.calendar.Schedule;

/**
 * @author Eitan Suez
 */
public interface Sheet
{
   public void addSchedule(Schedule schedule);
   public void removeSchedule(Schedule schedule);
   public void clearSchedules();
   public void setScheduleVisible(Schedule schedule, boolean visible);
   public TimeIntervalView getIntervalView();
   public void detach();
}
