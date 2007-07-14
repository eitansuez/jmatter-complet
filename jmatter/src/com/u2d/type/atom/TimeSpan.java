/*
 * Created on Apr 13, 2004
 */
package com.u2d.type.atom;

import com.u2d.element.CommandInfo;
import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TimeSpanInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.*;
import com.u2d.reflection.Cmd;

import java.util.*;
import java.text.*;

/**
 * @author Eitan Suez
 */
public class TimeSpan extends AbstractAtomicEO 
                      implements Searchable
{
   private Calendar _startCal;
   private Calendar _endCal;
   private transient TimeInterval _duration;
   private int _direction = 1;
   private transient boolean _sameDay;
   
   public static long ONEHOUR = 60 * 60 * 1000;

   public TimeSpan()
   {
      Calendar start = new TimeEO(8, 0).calendarValue();
      Calendar end = new TimeEO(9, 0).calendarValue();
      assign(start, end);
   }
   
   public TimeSpan(Date startDate, Date endDate)
   {
      Calendar start = Calendar.getInstance();
      start.setTime(startDate);

      Calendar end = Calendar.getInstance();
      end.setTime(endDate);
      
      assign(start, end);
   }
   
   public TimeSpan(Date startDate, long duration_milis)
   {
      this(startDate, new TimeInterval(duration_milis));
   }
   
   /**
    * @param startDate start date
    * @param duration in milis
    */
   public TimeSpan(Date startDate, TimeInterval duration)
   {
      Calendar start = Calendar.getInstance();
      start.setTime(startDate);

      Calendar end = Calendar.getInstance();
      end.setTimeInMillis(start.getTimeInMillis() + duration.getMilis());
      
      assign(start, end);
   }

   private void assign(Calendar start, Calendar end)
   {
      if (end.before(start))  // swap them
      {
         Calendar tmp = start;
         start = end;
         end = tmp;
         
         _direction = -1;
      }
      
      _startCal = start;
      _endCal = end;

      deriveDuration();
      deriveSameDay();
   }
   
   private void deriveDuration()
   {
      _duration = new TimeInterval(_endCal.getTimeInMillis() - _startCal.getTimeInMillis());
   }
   
   private void deriveSameDay()
   {
      _sameDay = sameDay(_startCal, _endCal);
   }
   public boolean isSameDay() { return _sameDay; }
   
   public static boolean sameDay(Calendar first, Calendar second)
   {
      return first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)
       && first.get(Calendar.MONTH) == second.get(Calendar.MONTH) 
       && first.get(Calendar.YEAR) == second.get(Calendar.YEAR);
   }
      
   public Date startDate() { return _startCal.getTime(); }
   public Date endDate() { return _endCal.getTime(); }
   
   public void startDate(Date startDate)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      assign(cal, _endCal);
      fireStateChanged();
   }
   public void endDate(Date endDate)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDate);
      assign(_startCal, cal);
      fireStateChanged();
   }
   
   // convenience..
   public Calendar startCal()
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(_startCal.getTime());
      return cal;
   }
   
   
   public TimeInterval duration() { return _duration; }
   public void setDuration(TimeInterval duration)
   {
      _endCal.setTimeInMillis(_startCal.getTimeInMillis() + duration.getMilis());
      assign(_startCal, _endCal);
      fireStateChanged();
   }
   
   
   public boolean contains(Date date)
   {
      // check no further than resolution of one second
      long item = date.getTime();
      long item_sec = item / 1000;
      long lowerbound_sec = _startCal.getTimeInMillis() / 1000;
      long upperbound_sec = _endCal.getTimeInMillis() / 1000;

         return ( item_sec < upperbound_sec && 
                  item_sec >  lowerbound_sec );
   }
   
   public boolean containsCompletely(TimeSpan span)
   {
      return contains(span.startDate()) && 
            contains(span.endDate());
   }
   
   public boolean containsOrIntersects(TimeSpan span)
   {
      return contains(span.startDate()) || contains(span.endDate());
   }
   
   
   public TimeSpan add(TimeInterval interval)
   {
      return add(interval.field(), (int) interval.amt());
   }

   public TimeSpan add(int field, int amount)
   {
      Calendar newStart = Calendar.getInstance();
      newStart.setTime(startDate());
      newStart.add(field, amount);
      return new TimeSpan(newStart.getTime(), duration());
   }
   
   public TimeSpan position(Date startDate)
   {
      return new TimeSpan(startDate, duration());
   }

   public TimeSpan next()
   {
      return add(duration());
   }

   public TimeSpan previous()
   {
      return add(_duration.field(), (int) (-1 * _duration.amt()));
   }

   public int numIntervals(TimeInterval interval)
   {
      long numIntervals = duration().getMilis() / interval.getMilis() ;
      return (int) ( numIntervals );
   }
   
   public double distance(TimeInterval interval)
   {
      return _direction * ((double) duration().getMilis()) / interval.getMilis();
   }
   
   public Iterator iterator(TimeInterval ti) { return new TimeIterator(ti); }

   
   class TimeIterator implements Iterator
   {
         TimeInterval _step;
         long _pointer;
         int _numIntervals;
         int _cursor;
      
         TimeIterator(TimeInterval step)
         {
            _step = step;
            _numIntervals = numIntervals(_step);
            _pointer = startDate().getTime();
            _cursor = 0;
         }
         
         public Object next()
         {
            long saved = _pointer;
            _pointer += _step.getMilis();
            _cursor++;
            return new TimeEO(saved);
         }
      
         public boolean hasNext()
         {
            return _cursor < _numIntervals;
         }
         
         public void remove() {} // not supported -- yeah i know i'm violation the def'n of this interface
   }
   
   
   public int validate()
   {
      int result = super.validate();
      if (result > 0)
         return result;
      
      if (_startCal.getTimeInMillis() > _endCal.getTimeInMillis())
      {
         fireValidationException("Start time must be before end time");
         return 1;
      }
      fireValidationException("");
      return 0;
   }

   public boolean isEmpty() { return false; }
   public void parseValue(String stringValue) throws ParseException {}

   public AtomicRenderer getRenderer() { return vmech().getTimeSpanRenderer(); }
   public AtomicEditor getEditor() { return vmech().getTimeSpanEditor(); }

   public EObject makeCopy()
   {
      return new TimeSpan(_startCal.getTime(), _endCal.getTime());
   }
   
   public void setValue(EObject value)
   {
      if (value == null) return; // attempt by hibernate to restore empty value - just ignore
      if (!(value instanceof TimeSpan))
         throw new IllegalArgumentException("Invalid type on set;  must be TimeSpan");
      if (value.equals(this)) return; // same.
      
      TimeSpan span = (TimeSpan) value;
      Calendar start = Calendar.getInstance();
      start.setTime(span.startDate());
      Calendar end = Calendar.getInstance();
      end.setTime(span.endDate());
      assign(start, end);
      fireStateChanged();
   }
   
   @Cmd
   public void PostponeOneHour(CommandInfo cmdInfo)
   {
      _startCal.add(Calendar.HOUR, 1);
      _endCal.add(Calendar.HOUR, 1);
      fireStateChanged();
   }
   

   public java.util.List getInequalities()
   {
      return new TimeSpanInequalities(field()).getInequalities();
   }
   
   public static TimeSpan today()
   {
      TimeEO startOfDay = new TimeEO(0, 0);
      TimeEO endOfDay = startOfDay.add(new TimeInterval(Calendar.HOUR, 24));
      return new TimeSpan(startOfDay.dateValue(), endOfDay.dateValue());
   }

   public String formatAsDate()
   {
      return DateEO.stdDateFormat().format(startDate());
   }

   public String toString()
   {
      String fromDate = DateEO.stdDateFormat().format(_startCal.getTime());
      String toDate = DateEO.stdDateFormat().format(_endCal.getTime());

      String fromTime = TimeEO.stdTimeFormat().format(_startCal.getTime());
      String toTime = TimeEO.stdTimeFormat().format(_endCal.getTime());

      if (isSameDay())
      {
         return fromDate + " " + fromTime + "-" + toTime;
      }
      else
      {
         return fromDate + " " + fromTime + "-" + toDate + " " + toTime;
      }
   }

   public Title title() { return new Title(toString()); }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof TimeSpan)) return false;
      TimeSpan span = (TimeSpan) obj;
      return (span.startDate().getTime() == _startCal.getTimeInMillis())
        && (span.endDate().getTime() == _endCal.getTimeInMillis());
   }

   public int hashCode()
   {
      return _startCal.hashCode() * 31 + _endCal.hashCode();
   }

}
