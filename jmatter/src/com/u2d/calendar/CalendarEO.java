package com.u2d.calendar;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 7, 2005
 * Time: 9:55:50 AM
 */
public abstract class CalendarEO extends AbstractComplexEObject
                                 implements Calendarable
{
   protected Calendrier _calendar = null;

   public Calendrier calendar()
   {
      if (_calendar == null)
         _calendar = new Calendrier(this);
      return _calendar;
   }

   @Cmd
   public Object ShowCalendar(CommandInfo cmdInfo)
   {
      if (schedulables() == null || schedulables().isEmpty())
      {
         return "Calendar has no schedulable items";
      }
      return calendar();
   }

}
