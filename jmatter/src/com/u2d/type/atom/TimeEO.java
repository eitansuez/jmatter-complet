/*
 * Created on Sep 17, 2003
 */
package com.u2d.type.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.*;

import java.util.*;
import java.text.*;

/**
 * @author Eitan Suez
 */
public class TimeEO extends AbstractAtomicEO
{
   private Calendar _cal;
   
   public static SimpleDateFormat PARSE_FORMAT = new SimpleDateFormat("H:mm");
   public static SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("h:mm a");
   
   public TimeEO()
   {
      _cal = Calendar.getInstance();
   }
   
   public TimeEO(long timeMillis)
   {
      this();
      _cal.setTimeInMillis(timeMillis);
   }
   
   public TimeEO(int hourofday, int minutes, int seconds)
   {
      this();
      _cal.set(Calendar.HOUR_OF_DAY, hourofday);
      _cal.set(Calendar.MINUTE, minutes);
      _cal.set(Calendar.SECOND, seconds);
   }
   
   public TimeEO(int hourofday, int minutes)
   {
      this(hourofday, minutes, 0);
   }
   
   public boolean isEmpty() { return _cal == null; }
   
   public SimpleDateFormat formatter()
   {
      SimpleDateFormat formatter = DISPLAY_FORMAT;
      if (field() != null && !StringEO.isEmpty(field().format()))
      {
         formatter = new SimpleDateFormat(field().format());
      }
      return formatter;
   }
   
   public void parseValue(String stringValue) throws ParseException
   {
      parse(formatter(), stringValue);
   }
   
   private void parse(DateFormat format, String value) throws ParseException
   {
      try
      {
         Date date = format.parse(value);
         _cal.setTime(date);
      }
      catch (ParseException ex)
      {
         if (format.equals(PARSE_FORMAT))
            throw new ParseException("Failed to parse value "+value, 0);
         parse(PARSE_FORMAT, value);
      }
   }

   public AtomicRenderer getRenderer() { return vmech().getTimeRenderer(); }
   public AtomicEditor getEditor() { return vmech().getTimeEditor(); }

   public EObject makeCopy()
   {
      long milis = this.dateValue().getTime();
      return new TimeEO(milis);
   }
   
   // ensure only a value < 24 hrs is returned (omit date part if any)
   private static long HRS24 = 24 * 60 * 60 * 1000;
   public long milisValue()
   {
      return _cal.getTimeInMillis() % HRS24;
   }
   public Date dateValue() { return _cal.getTime(); }
   public Calendar calendarValue() { return (Calendar) _cal.clone(); }
   
   public Date dateValue(Date base)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(base);
      cal.set(Calendar.HOUR_OF_DAY, _cal.get(Calendar.HOUR_OF_DAY));
      cal.set(Calendar.MINUTE, _cal.get(Calendar.MINUTE));
      cal.set(Calendar.SECOND, _cal.get(Calendar.SECOND));
      return cal.getTime();
   }
   
   public void setValue(long milis)
   {
      _cal.setTimeInMillis(milis);
      fireStateChanged();
   }
   public void setValue(Date date)
   {
      _cal.setTime(date);
      fireStateChanged();
   }
   public void setValue(Calendar cal)
   {
      _cal = (Calendar) cal.clone();
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (value == null) return; // attempt by hibernate to restore empty value - just ignore
      if (!(value instanceof TimeEO))
         throw new IllegalArgumentException("Invalid type on set;  must be TimeEO");
      setValue(((TimeEO) value).calendarValue());
   }
   
   public Title title()
   {
      if (_cal == null) return new Title("");
      
      String formattedString = formatter().format(_cal.getTime());
      return new Title(formattedString);
   }
   public String toString() { return title().toString(); }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof TimeEO)) return false;
      TimeEO time = (TimeEO) obj;
      return _cal.equals(time.calendarValue());
   }

   public int hashCode() { return _cal.hashCode(); }


   public void set(int field, int value)
   {
      _cal.set(field, value);
   }
   public void set(int field, Date from)
   {
      Calendar fromCal = Calendar.getInstance();
      fromCal.setTime(from);
      _cal.set(field, fromCal.get(field));
   }
   
   public TimeEO add(TimeInterval ti)
   {
      long milis = ti.getMilis() + dateValue().getTime();
      return new TimeEO(milis);
   }
   
   public long less(TimeEO other)
   {
      return assemble() - other.assemble();
   }
   
   public long assemble()
   {
      long hours = _cal.get(Calendar.HOUR_OF_DAY);
      long minutes = hours * 60 + _cal.get(Calendar.MINUTE);
      long seconds = minutes * 60 + _cal.get(Calendar.SECOND);
      long millis = seconds * 1000 + _cal.get(Calendar.MILLISECOND);
      return millis;
   }
   
}
