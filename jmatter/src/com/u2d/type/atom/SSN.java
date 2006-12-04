/*
 * Created on Jan 30, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.*;

/**
 * @author Eitan Suez
 */
public class SSN extends AbstractAtomicEO implements Searchable
{
   private String _value;
   
   public SSN()
   {
      _value = "";
   }
   public SSN(String value)
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
      if (value == null)
      {
         setValue("");
         return;
      }
      if (!(value instanceof SSN))
         throw new IllegalArgumentException("Invalid type on set;  must be SSN");
      setValue(((SSN) value).stringValue());
   }
   
   public boolean isEmpty()
   {
      return (_value == null) || ("".equals(_value.trim()));
   }
   
   private static String OMIT = "()-. ";
   private static String VALID = "0123456789";
   
   public int validate()
   {
      String value = SimpleParser.parseValue(OMIT, VALID, _value);
      if (value == null || value.length() != 9) return invalid();
      return 0;
   }
   
   private int invalid()
   {
      fireValidationException("Invalid SSN: "+_value);
      return 1;
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof SSN)) return false;
      SSN phone = (SSN) obj;
      return (_value.equals(phone.getValue()));
   }

   public int hashCode()
   {
      return _value.hashCode();
   }

   public String toString()
   {
      if (isEmpty()) return "";
      if (!isEmpty() && validate() > 0) return _value;
      String prefix = _value.substring(0,3);
      String middle = _value.substring(3,5);
      String suffix = _value.substring(5);
      return prefix + "-" + middle + "-" + suffix;
   }
   public Title title()
   {
      return new Title(toString());
   }


   public AtomicRenderer getRenderer() { return vmech().getSSNRenderer(); }
   public AtomicEditor getEditor() { return vmech().getSSNEditor(); }

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      if (stringValue == null || stringValue.trim().length() == 0)
      {
         setValue("");
         return;
      }
      
      String parsedValue = SimpleParser.parseValue(OMIT, VALID, stringValue);
      if (parsedValue == null || parsedValue.length() != 9)
      {
         throw new java.text.ParseException("Failed to parse SSN "+stringValue, 0);
      }
      setValue(parsedValue);
   }

   public EObject makeCopy()
   {
      return new SSN(this.stringValue());
   }
   
   
   // used by persist.HBMMaker..
   public static int getLength() { return 9; }
   
   // ===
   
   public java.util.List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }

}
