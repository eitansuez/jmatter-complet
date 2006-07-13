/*
 * Created on Jan 30, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;

/**
 * @author Eitan Suez
 */
public class USZipCode extends AbstractAtomicEO implements Searchable
{
   private String _value;
   
   public USZipCode()
   {
      _value = "";
   }
   public USZipCode(String value)
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
      if (!(value instanceof USZipCode))
         throw new IllegalArgumentException("Invalid type on set;  must be USPhone");
      setValue(((USZipCode) value).stringValue());
   }
   
   public boolean isEmpty()
   {
      return (_value == null) || ("".equals(_value.trim()));
   }
   
   private static String omit = "- ";
   private static String valid = "0123456789";
   
   public int validate()
   {
      String value = SimpleParser.parseValue(omit, valid, _value);
      if (value == null || value.length() != 5 && value.length() != 9) return invalid();
      return 0;
   }
   
   private int invalid()
   {
      fireValidationException("Invalid zip code: "+_value);
      return 1;
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof USZipCode)) return false;
      USZipCode phone = (USZipCode) obj;
      return (_value.equals(phone.getValue()));
   }
   
   public String toString()
   {
      if (isEmpty()) return "";
      if (!isEmpty() && validate() > 0) return _value;
      
      if (_value == "") return "";
      if (_value.length() == 5) return _value;
      String five = _value.substring(0, 5);
      String four = _value.substring(5);
      return five + "-" + four;
   }
   public Title title()
   {
      return new Title(toString());
   }


   public AtomicRenderer getRenderer() { return vmech().getUSZipRenderer(); }
   public AtomicEditor getEditor() { return vmech().getUSZipEditor(); }

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      if (stringValue == null || stringValue.trim().length() == 0)
      {
         setValue("");
         return;
      }
      
      String parsedValue = SimpleParser.parseValue(omit, valid, stringValue);
      if (parsedValue == null || parsedValue.length() != 5 && parsedValue.length() != 9)
      {
         throw new java.text.ParseException("Failed to parse zip code: "+stringValue, 0);
      }
      setValue(parsedValue);
   }

   public EObject makeCopy()
   {
      return new USZipCode(this.stringValue());
   }
   
   public static int getLength() { return 9; }

   // ==
   
   public java.util.List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }

   public int hashCode()
   {
      return _value.hashCode();
   }
}
