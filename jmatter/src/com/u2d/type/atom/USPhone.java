/*
 * Created on Jan 30, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;

/**
 * @author Eitan Suez
 */
public class USPhone extends AbstractAtomicEO implements Searchable
{
   private String _value;
   
   public USPhone()
   {
      _value = "";
   }
   public USPhone(String value)
   {
      _value = value;
   }
   
   public Object getValue() { return _value; }
   public String stringValue() { return _value; }
   
   public void setValue(String value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof USPhone))
         throw new IllegalArgumentException("Invalid type on set;  must be USPhone");
      setValue(((USPhone) value).stringValue());
   }
   
   public boolean isEmpty()
   {
      return StringEO.isEmpty(_value);
   }
   
   private static String omit = "()-. '";
   private static String valid = "0123456789";
   
   public int validate()
   {
      String value = SimpleParser.parseValue(omit, valid, _value);
      if (value == null || value.length() != 10) return invalid();
      return 0;
   }
   
   private int invalid()
   {
      fireValidationException("Invalid phone number: "+_value);
      return 1;
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof USPhone)) return false;
      USPhone phone = (USPhone) obj;
      return (_value.equals(phone.getValue()));
   }

   public int hashCode() { return _value.hashCode(); }


   public String toString()
   {
      if (isEmpty()) return "";
      if (!isEmpty() && validate() > 0) return _value;
      return "(" + areaCode() + ") " + localPart();
   }
   
   public String areaCode()
   {
      return _value.substring(0, 3);
   }
   public String localPart()
   {
      String prefix = _value.substring(3,6);
      String suffix = _value.substring(6);
      return prefix + "-" + suffix;
   }
   
   public Title title()
   {
      return new Title(toString());
   }

   public AtomicRenderer getRenderer() { return vmech().getUSPhoneRenderer(); }
   public AtomicEditor getEditor() { return vmech().getUSPhoneEditor(); }

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      if (StringEO.isEmpty(stringValue))
      {
         setValue("");
         return;
      }
      
      String parsedValue = SimpleParser.parseValue(omit, valid, stringValue);
      if (parsedValue == null || parsedValue.length() != 10)
      {
         throw new java.text.ParseException("Failed to parse phone number "+stringValue, 0);
      }
      setValue(parsedValue);
   }

   public EObject makeCopy()
   {
      return new USPhone(this.stringValue());
   }
   
   public static int getLength() { return 10; }

   // =====
   
   public java.util.List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }
}
