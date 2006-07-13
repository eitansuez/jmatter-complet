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
public class Email extends AbstractAtomicEO implements Searchable
{
   private String _value;
   
   public Email()
   {
      _value = "";
   }
   public Email(String value)
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
      if (!(value instanceof Email))
         throw new IllegalArgumentException("Invalid type on set;  must be Email");
      setValue(((Email) value).stringValue());
   }
   
   public Title title() {  return new Title(_value); }
   
   public boolean isEmpty()
   {
      return (_value == null) || ("".equals(_value.trim()));
   }
   
   public int validate()
   {
      int first = _value.indexOf("@");
      if (first < 1 || first == _value.length() - 1) return invalid();
      String latterpart = _value.substring(first + 1);
      
      if (_value.indexOf(" ")!=-1) return invalid();
      
      //System.out.println(latterpart);
      int idx = latterpart.indexOf(".");
      if (idx < 1 || idx == latterpart.length() - 1) return invalid();
      
      return 0;
   }
   
   private int invalid()
   {
      fireValidationException("Invalid email address syntax: "+_value);
      return 1;
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof Email)) return false;
      Email email = (Email) obj;
      return (_value.equals(email.getValue()));
   }

   public int hashCode() { return _value.hashCode(); }

   public String toString() { return _value; }

   public AtomicRenderer getRenderer() { return vmech().getEmailRenderer(); }
   public AtomicEditor getEditor() { return vmech().getEmailEditor(); }

   public void parseValue(String stringValue)
   {
      setValue(stringValue);
   }

   public EObject makeCopy()
   {
      return new Email(this.stringValue());
   }
   
   // ===
   
   public java.util.List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }


}
