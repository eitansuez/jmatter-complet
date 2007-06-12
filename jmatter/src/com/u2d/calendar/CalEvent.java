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

   private static Map schedulableFieldnames = new HashMap();

   public static String schedulableFieldname(Class cls)
   {
      if (schedulableFieldnames.get(cls) == null)
      {
         schedulableFieldnames.put(cls, (String)
               Harvester.introspectField(cls, "schedulableFieldname"));
      }
      return (String) schedulableFieldnames.get(cls);
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

   public static long DEFAULT_DURATION = TimeSpan.ONEHOUR;
   @Cmd
   public static void SetDefaultDurationMins(CommandInfo cmdInfo,
                                @Arg("Duration (Minutes)") IntEO durationMins)
   {
      DEFAULT_DURATION = (long) ( durationMins.intValue() * 60 * 1000 );
   }
}
