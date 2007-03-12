package com.u2d.calendar;

import com.u2d.type.atom.TimeInterval;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 12, 2007
 * Time: 5:07:18 PM
 */
public enum CellResChoice
{
   ONE_MINUTE(1), TWO_MINUTES(2), FIVE_MINUTES(5),
     TEN_MINUTES(10), TWENTY_MINUTES(15), THIRTY_MINUTES(30), ONE_HOUR(60);

   private int _minutes;
   
   CellResChoice(int minutes)
   {
      _minutes = minutes;
   }
   
   public int minutes() { return _minutes; }
   
   public TimeInterval timeInterval()
   {
      return new TimeInterval(Calendar.MINUTE, _minutes);
   }
   
   public String toString()
   {
      return _minutes + " min.";
   }

}