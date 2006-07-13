/*
 * Created on Apr 13, 2004
 */
package com.u2d.calendar;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.Harvester;
import com.u2d.type.atom.*;
import com.u2d.view.*;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.CommandAt;
import com.u2d.reflection.ParamAt;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Eitan Suez
 */
public abstract class CalEvent extends AbstractComplexEObject
{
   public abstract Title calTitle();

   public ComplexEView getCalEventView() { return vmech().getCalEventView(this); }
   public ComplexEView getCalEventView(Schedule schedule)
   {
      return vmech().getCalEventView(this, schedule);
   }

   private static Map timespanFieldnames = new HashMap();
   private static Map schedulableFieldnames = new HashMap();

   public static String timespanFieldname(Class cls)
   {
      if (timespanFieldnames.get(cls) == null)
      {
         timespanFieldnames.put(cls, (String)
               Harvester.introspectField(cls, "timespanFieldname"));
      }
      return (String) timespanFieldnames.get(cls);
   }
   public static String schedulableFieldname(Class cls)
   {
      if (schedulableFieldnames.get(cls) == null)
      {
         schedulableFieldnames.put(cls, (String)
               Harvester.introspectField(cls, "schedulableFieldname"));
      }
      return (String) schedulableFieldnames.get(cls);
   }
   
   public String timespanFieldname() { return timespanFieldname(getClass()); }
   public String schedulableFieldname() { return schedulableFieldname(getClass()); }

   public TimeSpan timeSpan()
   {
      return (TimeSpan) field(timespanFieldname()).get(this);
   }
   public void timeSpan(TimeSpan span)
   {
      field(timespanFieldname()).set(this, span);
   }


   public Schedulable schedulable()
   {
      return (Schedulable) field(schedulableFieldname()).get(this);
   }
   public void schedulable(Schedulable schedulable)
   {
      field(schedulableFieldname()).set(this, schedulable);
   }

   public static long DEFAULT_DURATION = TimeSpan.ONEHOUR;
   @CommandAt
   public static void SetDefaultDurationHrs(CommandInfo cmdInfo,
                                @ParamAt("Duration (Hrs)") FloatEO durationHrs)
   {
      DEFAULT_DURATION = (long) ( durationHrs.doubleValue() * 3600 * 1000 );
   }
}
