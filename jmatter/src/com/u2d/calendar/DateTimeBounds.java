package com.u2d.calendar;

import com.u2d.type.atom.DateEO;
import com.u2d.type.atom.TimeEO;
import com.u2d.type.atom.TimeInterval;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 12, 2007
 * Time: 5:18:46 PM
 */
public class DateTimeBounds
{
   public static final TimeInterval DEFAULT_DAY_INTERVAL = new TimeInterval(Calendar.HOUR, 12);
   public static final TimeEO DEFAULT_DAY_START_TIME = new TimeEO(7, 0, 0);
   public static final TimeInterval DEFAULT_WEEK_INTERVAL = new TimeInterval(Calendar.DATE, 7);
   public static final TimeEO DEFAULT_WEEK_START_TIME = new TimeEO(0, 0, 0);

   private final DateEO _position = new DateEO(new Date());
   private final TimeEO _weekStartTime = new TimeEO(0, 0, 0);
   private final TimeEO _dayStartTime = new TimeEO(7, 0, 0);
   private TimeInterval _weekInterval = new TimeInterval(Calendar.DATE, 7);
   private TimeInterval _dayInterval = new TimeInterval(Calendar.HOUR, 12);
   private CellResChoice _resolution = CellResChoice.THIRTY_MINUTES;

   public DateTimeBounds(DateEO position,
                         TimeEO weekStartTime, TimeInterval weekInterval,
                         TimeEO dayStartTime, TimeInterval dayInterval)
   {
      _position.setValue(position);
      _weekStartTime.setValue(weekStartTime);
      _weekInterval = weekInterval;
      _dayStartTime.setValue(dayStartTime);
      _dayInterval = dayInterval;
   }

   /**
    * Use default cell resolution
    */
   public DateTimeBounds(DateEO position,
                         TimeEO weekStartTime, TimeInterval weekInterval,
                         TimeEO dayStartTime, TimeInterval dayInterval,
                         CellResChoice resolution)
   {
      this(position, weekStartTime, weekInterval, dayStartTime, dayInterval);
      _resolution = resolution;
   }

   /**
    *  Use default week values
    */
   public DateTimeBounds(DateEO position,
                         TimeEO dayStartTime, TimeInterval dayInterval)
   {
      this(position, DEFAULT_WEEK_START_TIME, DEFAULT_WEEK_INTERVAL,
                     dayStartTime, dayInterval);
   }

   /**
    *   use defaults for both day and week values
    */
   public DateTimeBounds(DateEO position)
   {
      this(position, DEFAULT_DAY_START_TIME, DEFAULT_DAY_INTERVAL);
   }
   
   public DateTimeBounds()
   {
      this(new DateEO(new Date()));
   }

   
   
   public DateEO position() { return _position; }
   public void position(Date date) { _position.setValue(date); }

   public TimeEO weekStartTime() { return _weekStartTime; }
   public void weekStartTime(Date date) { _weekStartTime.setValue(date); }

   public TimeEO dayStartTime() { return _dayStartTime; }
   public void dayStartTime(Date date) { _dayStartTime.setValue(date); }

   public TimeInterval weekInterval() { return _weekInterval; }
   public void weekInterval(int field, long amount) { _weekInterval = new TimeInterval(field, amount); }

   public TimeInterval dayInterval() { return _dayInterval; }
   public void dayInterval(int field, long amount) { _dayInterval = new TimeInterval(field, amount); }

   public CellResChoice resolution() { return _resolution; }
   public void resolution(CellResChoice resolution) { _resolution = resolution; }

}
