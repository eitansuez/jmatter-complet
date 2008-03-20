/*
 * Created on Apr 13, 2004
 */
package com.u2d.calendar;

import com.u2d.model.*;
import com.u2d.type.atom.*;
import com.u2d.view.*;
import com.u2d.element.CommandInfo;
import com.u2d.element.Field;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Arg;
import com.u2d.find.SimpleQuery;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Eitan Suez
 */
public abstract class CalEvent extends AbstractComplexEObject
{
   /**
    * To customize title for an instance in the context of a calendar view,
    * override this method.  Otherwise, calendar title falls back to default
    * title().
    */
   public Title calTitle() { return title(); }


   public ComplexEView getCalEventView() { return vmech().getCalEventView(this); }
   public ComplexEView getCalEventView(Schedule schedule)
   {
      return vmech().getCalEventView(this, schedule);
   }

   private static Map<Class, String> schedulableFieldnames = new HashMap<Class, String>();

   public static String schedulableFieldname(Class cls)
   {
      if (!schedulableFieldnames.containsKey(cls))
      {
         schedulableFieldnames.put(cls, (String)
               Harvester.introspectField(cls, "schedulableFieldname"));
      }
      return schedulableFieldnames.get(cls);
   }
   
   public String schedulableFieldname() { return schedulableFieldname(getClass()); }
   
   private Field timespanField()
   {
      return type().firstFieldOfType(TimeSpan.class);
   }
   public String timespanFieldname() { return timespanField().name(); }
   public static String timespanFieldname(Class cls)
   {
      return ComplexType.forClass(cls).firstFieldOfType(TimeSpan.class).name();
   }

   public TimeSpan timeSpan()
   {
      return (TimeSpan) timespanField().get(this);
   }
   public void timeSpan(TimeSpan span)
   {
      timespanField().set(this, span);
   }


   public Schedulable schedulable()
   {
      EObject schedulable = field(schedulableFieldname()).get(this);
      if (schedulable.isEmpty()) return null;
      return (Schedulable) schedulable;
   }
   public void schedulable(Schedulable schedulable)
   {
      field(schedulableFieldname()).set(this, schedulable);
   }

   // TODO: add support for auto-persisting static fields to db.
   public static long DEFAULT_DURATION = TimeSpan.ONEHOUR;
   @Cmd
   public static void SetDefaultDurationMins(CommandInfo cmdInfo,
                                @Arg("Duration (Minutes)") IntEO durationMins)
   {
      DEFAULT_DURATION = (long) ( durationMins.intValue() * 60 * 1000 );
   }
   
   @Cmd
   public static CalEventList BrowseInCalendar(CommandInfo cmdInfo, ComplexType targetType)
   {
      TimeSpan span = new TimeSpan(new Date(), new TimeInterval(Calendar.DATE, 7));
      return new CalEventList(new SimpleQuery(targetType), span);
   }
   
}
