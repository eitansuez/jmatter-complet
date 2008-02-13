/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.DateInequalities;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import java.util.*;
import java.text.*;

/**
 * Represents a date and a time where both are important.  e.g. as
 *  in the start date & time for a meeting or visit.
 * 
 * @author Eitan Suez
 */
public class DateTime extends AbstractAtomicEO implements Searchable
{
   private Date _value;

   private static DateFormat _dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
   private static DateFormat _noyear = new SimpleDateFormat("MM/dd HH:mm");
   private static DateFormat _2digityear = new SimpleDateFormat("MM/dd/yy HH:mm");
   private static Calendar NOW = Calendar.getInstance();

   public DateTime() {}
   
   public DateTime(Date value)
   {
      _value = value;
   }
   
   public Date dateValue() { return _value; }
   public void setValue(Date value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof DateTime))
         throw new IllegalArgumentException("Invalid type on set;  must be DateTime");
      setValue(((DateTime) value).dateValue());
   }
   
   public boolean isEmpty() { return _value == null; }
   
   public Title title()
   {
      if (_value == null) return new Title("");
      String formattedString = _dateFormat.format(_value);
      return new Title(formattedString);
   }
   public String toString()
   {
      return title().toString();
   }

   public AtomicRenderer getRenderer() { return vmech().getDateTimeRenderer(); }
   public AtomicEditor getEditor() { return vmech().getDateTimeEditor(); }

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      if (StringEO.isEmpty(stringValue))
      {
         _value = null;
         return;
      }
      
      try
      {
         Date value = _dateFormat.parse(stringValue);
         Calendar cal = Calendar.getInstance();
         cal.setTime(value);
         if (cal.get(Calendar.YEAR) < 100)  // was a 2-digit year
         {
            value = _2digityear.parse(stringValue);
         }
         setValue(value);
      }
      catch (java.text.ParseException ex)
      {
         Date value = _noyear.parse(stringValue);
         Calendar cal = Calendar.getInstance();
         cal.setTime(value);
         cal.set(Calendar.YEAR, NOW.get(Calendar.YEAR));  // default current year (w/o this code uses epoch)
         setValue(cal.getTime());
      }
      
   }

   public EObject makeCopy()
   {
      return new DateTime(this.dateValue());
   }

   // ==
   
   public java.util.List getInequalities()
   {
      return new DateInequalities(field(), false).getInequalities();
   }

   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof DateTime)) return false;
      return _value == ((DateTime) obj).dateValue();
   }

   public int hashCode()
   {
      return _value.hashCode();
   }
}
