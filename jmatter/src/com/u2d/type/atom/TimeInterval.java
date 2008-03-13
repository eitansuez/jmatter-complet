/*
 * Created on Sep 17, 2003
 */
package com.u2d.type.atom;

import java.util.*;

/**
 * @author Eitan Suez
 */
public class TimeInterval
{
   private long _milis = 0;
   
   public static TimeInterval ONEDAY = new TimeInterval(Calendar.DATE,  1);
   
   private int _field;
   private long _amt;
   
   public TimeInterval(int field, long amount)
   {
      _field = field;
      _amt = amount;
      _milis = calcMilis(field, amount);
   }
   public TimeInterval(long milis) { _milis = milis; }
   
   private long calcMilis(int field, long amount)
   {
      long milis = amount;
      
      switch(field)
      {
         case Calendar.MONTH:
            milis *= 30;
         case Calendar.DATE:
            milis *= 24;
         case Calendar.HOUR:
         case Calendar.HOUR_OF_DAY:
            milis *= 60;
         case Calendar.MINUTE:
            milis *= 60;
         case Calendar.SECOND:
            milis *= 1000;
      }

      return milis;
   }
   
   public TimeInterval add(int field, long amount)
   {
      return new TimeInterval(_milis + calcMilis(field, amount));
   }
   public TimeInterval add(TimeInterval ti)
   {
      return add(ti.field(), ti.amt());
   }
   
   public int field() { return _field; }
   public long amt() { return _amt; }
   
   public long getMilis() { return _milis; }
   
   class ConventionalInterval
   {
      String name;
      int multiplier;
      
      ConventionalInterval(String name, int multiplier)
      {
         this.name = name;
         this.multiplier = multiplier;
      }
   }
   
   ConventionalInterval YEAR = new ConventionalInterval("years", 1);
   ConventionalInterval MONTH = new ConventionalInterval("months", 12);
   ConventionalInterval DAY = new ConventionalInterval("days", 30);
   ConventionalInterval HOUR = new ConventionalInterval("hours", 24);
   ConventionalInterval MINUTE = new ConventionalInterval("minutes", 60);
   ConventionalInterval SECOND = new ConventionalInterval("seconds", 60);
   ConventionalInterval MILIS = new ConventionalInterval("milis", 1000);
   
   ConventionalInterval[] cogs = {SECOND, MINUTE, HOUR, DAY, MONTH, YEAR};
   
   public String toString()
   {
      long value = _milis / MILIS.multiplier;
      long remainder;
      String result = "";
      
      for (int i=0; i<cogs.length; i++)
      {
         remainder = value % cogs[i].multiplier;
         
         if (result == "") result = remainder + " " + cogs[i].name;
         else result = remainder + " " + cogs[i].name + ", " + result;
         
         value = value / cogs[i].multiplier;
         if (value == 0) break;
      }
      return result;
   }
   
   public String toHrMinFormat()
   {
      long minutes = _milis / ( SECOND.multiplier * MILIS.multiplier);
      int mins = (int) (minutes % MINUTE.multiplier);
      int hrs = (int) (minutes / MINUTE.multiplier);
      return StringEO.zeroPad(hrs,2)+":"+StringEO.zeroPad(mins,2)+" hrs";
   }

   public boolean equals(Object obj)
   {
      if (obj == null || (!(obj instanceof TimeInterval))) return false;
      TimeInterval other = (TimeInterval) obj;
      return _milis == other.getMilis();
   }

   public int hashCode()
   {
      return new Long(_milis).hashCode() * 23;
   }
}
