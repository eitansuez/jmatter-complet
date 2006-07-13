/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.DateInequalities;
import com.u2d.model.AtomicRenderer;
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
   protected Date _value = null;  // _value of null implies empty (not set)
   private Calendar _calendar;

   public DateEO()
   {
      _calendar = Calendar.getInstance();
   }

   public DateEO(Date value)
   {
      this();
      _value = value;
      if (value != null)
      {
         _calendar.setTime(_value);
      }
   }
   
   // ensure that hours/minutes/seconds set to 0
   protected static long HRS24 = 24 * 60 * 60 * 1000;
   public long milisValue()
   {
      long milis = _value.getTime();
      long remainder = milis % HRS24;
      return milis - remainder;
   }
   public Date dateValue() { return _value; }
   public void setValue(Date value)
   {
      _value = value;
      if (_value != null)
      {
         _calendar.setTime(_value);
      }
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (value == null) return; // attempt by hibernate to restore empty value - just ignore
      if (!(value instanceof DateEO))
         throw new IllegalArgumentException("Invalid type ("+value.getClass()+") on set;  must be DateEO");
      setValue(((DateEO) value).dateValue());
   }
   
   public int dayofmonth() { return _calendar.get(Calendar.DAY_OF_MONTH); }
   public int month() { return _calendar.get(Calendar.MONTH); }
   public int year() { return _calendar.get(Calendar.YEAR); }
   
   public static int daysDifference(DateEO first, DateEO second)
   {
      Date date1 = first.dateValue();
      Date date2 = second.dateValue();
      long diff = date1.getTime() - date2.getTime();
      return Math.abs((int) (diff / HRS24));
   }

   public void add(TimeInterval interval)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(_value);
      cal.add(interval.field(), (int) interval.amt());
      setValue(cal.getTime());
//      long milis = _value.getTime() + interval.getMilis();
//      setValue(new Date(milis));
   }
   public void subtract(TimeInterval interval)
   {
//      long milis = _value.getTime() - interval.getMilis();
//      setValue(new Date(milis));
      add(new TimeInterval(interval.field(), - 1 * interval.amt()));
   }
   
   public boolean isEmpty() { return _value == null; }
   
   public Title title()
   {
      if (_value == null) return new Title("");
      String formattedString = STANDARD.format(_value);
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

   public int hashCode() { return _value.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getDateRenderer(); }
   public AtomicEditor getEditor() { return vmech().getDateEditor(); }

   private static DateFormat NODELIMITERS, STANDARD, TWODIGITYEAR, NOYEAR;
   static
   {
      NODELIMITERS = new SimpleDateFormat("MMddyyyy");
      STANDARD = new SimpleDateFormat("MM/dd/yyyy");
      TWODIGITYEAR = new SimpleDateFormat("MM/dd/yy");
      NOYEAR = new SimpleDateFormat("MM/dd");
   }
   
   private DateFormatStrategy NODELIMS_STRATEGY, STD_STRATEGY, 
   TWODIGITS_STRATEGY, NOYR_STRATEGY;
   {
      NODELIMS_STRATEGY = new NoDelimsStrategy();
      STD_STRATEGY = new StandardStrategy();
      TWODIGITS_STRATEGY = new SimpleDateFormatStrategy(TWODIGITYEAR);
      NOYR_STRATEGY = new NoYearStrategy();
   }
   private DateFormatStrategy[] strategyFallbackHierarchy = 
   {NODELIMS_STRATEGY, STD_STRATEGY, TWODIGITS_STRATEGY, NOYR_STRATEGY};

   private static int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
   

   public void parseValue(String stringValue) throws ParseException
   {
      if (StringEO.isEmpty(stringValue))
      {
         setValue((Date) null);
         return;
      }
      for (int i=0; i<strategyFallbackHierarchy.length; i++)
         if (strategyFallbackHierarchy[i].parse(stringValue)) return;
      
      throw new ParseException("Failed to parse date value: "+stringValue, 0);
   }
   
   interface DateFormatStrategy
   {
      public boolean parse(String stringValue);
   }
   class StandardStrategy implements DateFormatStrategy
   {
      public boolean parse(String stringValue)
      {
         try
         {
            Date value = STANDARD.parse(stringValue);
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            if (cal.get(Calendar.YEAR) < 100)  // a 2-digit year was entered
            {
               return false;
            }
            setValue(value);
            return true;
         }
         catch (ParseException ex)
         {
            return false;
         }
      }
   }
   class SimpleDateFormatStrategy implements DateFormatStrategy
   {
      DateFormat _fmt;
      SimpleDateFormatStrategy(DateFormat fmt) { _fmt = fmt; }
      
      public boolean parse(String stringValue)
      {
         try
         {
            setValue(_fmt.parse(stringValue));
            return true;
         }
         catch (ParseException ex)
         {
            return false;
         }
      }
   }
   class NoYearStrategy implements DateFormatStrategy
   {
      public boolean parse(String stringValue)
      {
         try
         {
            Date value = NOYEAR.parse(stringValue);
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            cal.set(Calendar.YEAR, CURRENT_YEAR);  // default current year (w/o this code uses epoch)
            setValue(cal.getTime());
            return true;
         }
         catch (ParseException ex)
         {
            return false;
         }
      }
   }
   class NoDelimsStrategy implements DateFormatStrategy
   {
      public boolean parse(String stringValue)
      {
         try
         {
            if (stringValue.length() == 7)  // zero-padding for parse
               stringValue = "0" + stringValue;
            
            Date value = NODELIMITERS.parse(stringValue);
            setValue(value);
            return true;
         }
         catch (ParseException ex)
         {
            return false;
         }
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
