/*
 * Created on Mar 31, 2004
 */
package com.u2d.type.atom;

import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;

/**
 * @author Eitan Suez
 */
public class CharEO extends AbstractAtomicEO
{
   private char _value;
   
   public CharEO() {}

   public CharEO(char value)
   {
      _value = value;
   }
   
   public char charValue() { return _value; }
   public String stringValue()
   {
      return Character.toString(_value).trim();
   }
   
   public void setValue(char value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof CharEO))
         throw new IllegalArgumentException("Invalid type on set;  must be CharEO");
      setValue(((CharEO) value).charValue());
   }
   
   public Title title() { return new Title(stringValue()); }
   public String toString() { return stringValue(); }
   
   public boolean isEmpty()
   {
      return isEmpty(_value);
   }
   public static boolean isEmpty(char value)
   {
      return Character.isWhitespace(value);
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof CharEO)) return false;
      return _value == ((CharEO) obj).charValue();
   }

   public int hashCode() { return (int) _value; }

   public AtomicRenderer getRenderer() { return vmech().getCharRenderer(); }
   public AtomicEditor getEditor() { return vmech().getCharEditor(); }

   public void parseValue(String stringValue)
   {
      if (stringValue == null || stringValue.trim().length() == 0)
      {
         setValue(' ');
         return;
      }
      char charVal = stringValue.trim().charAt(0);
      setValue(charVal);
   }
   
   public EObject makeCopy()
   {
      return new CharEO(this.charValue());
   }

}
