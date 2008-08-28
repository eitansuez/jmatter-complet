/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.DateInequalities;
import com.u2d.model.*;
import java.util.*;
import java.text.*;

/**
 * Represents a Date (time information unimportant. e.g. a date of birth)
 * 
 * @author Eitan Suez
 */
public class DateEO extends AbstractAtomicEO implements Searchable, Comparable<DateEO>
{
   private Calendar _value = null;

   public DateEO() { }

   public DateEO(Date date)
   {
      if (date == null)
      {
         return;
      }
      _value = Calendar.getInstance();
      _value.setTime(date);
   }
   
   // ensure that hours/minutes/seconds set to 0
   protected static long HRS24 = 24 * 60 * 60 * 1000;
   public long milisValue()
   {
      if (_value == null) return 0;
      long milis = _value.getTimeInMillis();
      long remainder = milis % HRS24;
      return milis - remainder;
   }

   public Date dateValue()
   {
      if (_value == null) return null;
      return _value.getTime();
   }

   public Calendar calendarValue()
   {
      if (_value == null) return null;
      Calendar cal = Calendar.getInstance();
      cal.setTime(_value.getTime());
      return cal;
   }
   public void setValue(Date value)
   {
      if (value == null)
      {
         _value = null;
      }
      else
      {
         if (_value == null)
         {
             _value = Calendar.getInstance();
         }
         _value.setTime(value);
      }
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (value == null)
      {
         _value = null;
         return;
      }
      if (!(value instanceof DateEO))
         throw new IllegalArgumentException("Invalid type ("+value.getClass()+") on set;  must be DateEO");
      setValue(((DateEO) value).dateValue());
   }

   public int dayofmonth()
   {
      if (_value == null) return 0;
      return _value.get(Calendar.DAY_OF_MONTH);
   }

   public int weekofyear()
   {
      if (_value == null) return 0;
      return _value.get(Calendar.WEEK_OF_YEAR);
   }

   public int month()
   {
      if (_value == null) return 0;
      return _value.get(Calendar.MONTH);
   }

   public int year()
   {
      if (_value == null) return 0;
      return _value.get(Calendar.YEAR);
   }

   public static int daysDifference(DateEO first, DateEO second)
   {
      Date date1 = first.dateValue();
      Date date2 = second.dateValue();
      long diff = date1.getTime() - date2.getTime();
      return Math.abs((int) (diff / HRS24));
   }

   public void add(TimeInterval interval)
   {
      if (_value == null) throw new IllegalArgumentException("Cannot add to a null date");
      _value.add(interval.field(), (int) interval.amt());
      fireStateChanged();
//      long milis = _value.getTime() + interval.getMilis();
//      setValue(new Date(milis));
   }
   public void subtract(TimeInterval interval)
   {
      if (_value == null) throw new IllegalArgumentException("Cannot subtract from a null date");
//      long milis = _value.getTime() - interval.getMilis();
//      setValue(new Date(milis));
      add(new TimeInterval(interval.field(), - 1 * interval.amt()));
   }
   
   public boolean isEmpty() { return _value == null; }
   
   public SimpleDateFormat formatter()
   {
      SimpleDateFormat formatter = STANDARD;
      if (field() != null && !StringEO.isEmpty(field().format()))
      {
         formatter = fieldFormatter();
      }
      return formatter;
   }
   public SimpleDateFormat fieldFormatter()
   {
      if (field() == null || StringEO.isEmpty(field().format()))
      {
         return null;
      }
      else
      {
         return new SimpleDateFormat(field().format());
      }
   }
   
   public Title title()
   {
      if (_value == null) return new Title("--");
      String formattedString = formatter().format(_value.getTime());
      return new Title(formattedString);
   }
   public String toString() { return title().toString(); }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof DateEO)) return false;
      DateEO otherDate = (DateEO) obj;
      // don't care about time, just year, month, day
      return (   (year() == otherDate.year()) &&
                 (month() == otherDate.month()) &&
                 (dayofmonth() == otherDate.dayofmonth())
             );
   }

   public int hashCode()
   {
      // problem!  because DateEO is mutable, have nothing to hold on to.  cannot base hashcode on a mutable value.
      if (_value == null) return 13;
      return _value.hashCode();
   }

   public AtomicRenderer getRenderer() { return vmech().getDateRenderer(); }
   public AtomicEditor getEditor() { return vmech().getDateEditor(); }

   private static SimpleDateFormat NODELIMITERS, STANDARD, TWODIGITYEAR, NOYEAR;
   static
   {
      NODELIMITERS = new SimpleDateFormat("MMddyyyy");
      STANDARD = new SimpleDateFormat("MM/dd/yyyy");
      TWODIGITYEAR = new SimpleDateFormat("MM/dd/yy");
      NOYEAR = new SimpleDateFormat("MM/dd");
   }
   public static void setStandardDateFormat(String format)
   {
      STANDARD = new SimpleDateFormat(format);
      strategyFallbackHierarchy = new DateFormatStrategy[] {STD_STRATEGY};
   }
   
   public static SimpleDateFormat stdDateFormat() { return STANDARD; }
   
   private static DateFormatStrategy NODELIMS_STRATEGY = new NoDelimsStrategy();
   private static DateFormatStrategy STD_STRATEGY = new StandardStrategy();
   private static DateFormatStrategy TWODIGITS_STRATEGY = new SimpleDateFormatStrategy(TWODIGITYEAR);
   private static DateFormatStrategy NOYR_STRATEGY = new NoYearStrategy();
   
   private static DateFormatStrategy[] strategyFallbackHierarchy = 
             {NODELIMS_STRATEGY, STD_STRATEGY, TWODIGITS_STRATEGY, NOYR_STRATEGY};

   private static int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
   

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      if (StringEO.isEmpty(stringValue))
      {
         setValue((Date) null);
         return;
      }
      DateFormat fieldFormatter = fieldFormatter();
      if (fieldFormatter == null)
      {
         for (int i=0; i<strategyFallbackHierarchy.length; i++)
            if (strategyFallbackHierarchy[i].parse(stringValue, this)) return;
         throw new java.text.ParseException(ComplexType.localeLookupStatic("error.date")+": "+stringValue, 0);
      }
      else
      {
         Date value = fieldFormatter.parse(stringValue);
         Calendar cal = Calendar.getInstance();
         cal.setTime(value);
         setValue(value);
      }
   }
   
   interface DateFormatStrategy
   {
      public boolean parse(String stringValue, DateEO target);
   }
   static abstract class BaseDateFormatStrategy implements DateFormatStrategy
   {
      public boolean parse(String stringValue, DateEO target)
      {
         try
         {
            return tryParse(stringValue, target);
         }
         catch (java.text.ParseException ex)
         {
            return false;
         }
      }
      protected abstract boolean tryParse(String stringValue, DateEO target) throws java.text.ParseException;
   }
   static class StandardStrategy extends BaseDateFormatStrategy
   {
      public boolean tryParse(String stringValue, DateEO target) throws java.text.ParseException
      {
         Date value = STANDARD.parse(stringValue);
         Calendar cal = Calendar.getInstance();
         cal.setTime(value);
         if (cal.get(Calendar.YEAR) < 100)  // a 2-digit year was entered
         {
            return false;
         }
         target.setValue(value);
         return true;
      }
   }
   static class SimpleDateFormatStrategy extends BaseDateFormatStrategy
   {
      DateFormat _fmt;
      SimpleDateFormatStrategy(DateFormat fmt) { _fmt = fmt; }
      
      public boolean tryParse(String stringValue, DateEO target) throws java.text.ParseException
      {
         target.setValue(_fmt.parse(stringValue));
         return true;
      }
   }
   static class NoYearStrategy extends BaseDateFormatStrategy
   {
      public boolean tryParse(String stringValue, DateEO target) throws java.text.ParseException
      {
         Date value = NOYEAR.parse(stringValue);
         Calendar cal = Calendar.getInstance();
         cal.setTime(value);
         cal.set(Calendar.YEAR, CURRENT_YEAR);  // default current year (w/o this code uses epoch)
         target.setValue(cal.getTime());
         return true;
      }
   }
   static class NoDelimsStrategy extends BaseDateFormatStrategy
   {
      public boolean tryParse(String stringValue, DateEO target) throws java.text.ParseException
      {
         if (stringValue.length() == 7)  // zero-padding for parse
            stringValue = "0" + stringValue;
         
         Date value = NODELIMITERS.parse(stringValue);
         target.setValue(value);
         return true;
      }
   }
   
   
   // ======
   
   public EObject makeCopy()
   {
      return new DateEO(this.dateValue());
   }
   
   // ==
   
   public java.util.List getInequalities()
   {
      return new DateInequalities(field(), true).getInequalities();
   }

   public static Class getCustomTypeImplementorClass()
   {
      return com.u2d.persist.type.DateEOUserType.class;
   }


   // == age-related logic:

   private long getAgeMilis()
   {
      Date now = new Date();
      return getAgeMilisAtDate(now);
   }
   
   private long getAgeMilisAtDate(Date date)
   {
      return date.getTime() - milisValue();
   }

   private static long SECONDSINAYEAR = 31556926;
   private int getAgeYears()
   {
      Date now = new Date();
      return getAgeYearsAtDate(now);
   }

   public int getAge() { return getAgeYears(); }
   
   public int getAgeYearsAtDate(DateEO dateEO)
   {
      return getAgeYearsAtDate(dateEO.dateValue());
   }
   public int getAgeYearsAtDate(Date date)
   {
      return (int) ( getAgeMilisAtDate(date) / (1000 * SECONDSINAYEAR) );
   }

   public String ageString()
   {
      Date now = new Date();
      return ageAtDateString(now);
   }
   
   public String ageAtDateString(Date date)
   {
      if (_value == null) return "";
      return "(" + getAgeYearsAtDate(date) + " yrs old)";
   }

   public int compareTo(DateEO anotherDateEO)
   {
      return dateValue().compareTo(anotherDateEO.dateValue());
   }
   
   public static DateEO today()
   {
      DateEO deo = new DateEO();
      deo.setValue(new Date());
      return deo;
   }

}
