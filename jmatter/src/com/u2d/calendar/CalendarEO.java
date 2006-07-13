package com.u2d.calendar;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.CommandAt;

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

   @CommandAt
   public Calendrier ShowCalendar(CommandInfo cmdInfo)
   {
      return calendar();
   }

}
