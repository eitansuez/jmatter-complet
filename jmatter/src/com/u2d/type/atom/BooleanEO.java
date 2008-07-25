/*
 * Created on Jan 31, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.IdentityInequality;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;

/**
 * @author Eitan Suez
 */
public class BooleanEO extends AbstractAtomicEO implements Searchable
{
   private boolean _value;
   
   public BooleanEO() { _value = false; }
   public BooleanEO(boolean value) { _value = value; }
   
   public boolean booleanValue() { return _value; }
   public void setValue(boolean value)
   {
      if (_value == value) return;
      _value = value;
      fireStateChanged();
   }
   
   public void setValue(EObject value)
   {
      if (!(BooleanEO.class.isAssignableFrom(value.getClass())))
         throw new IllegalArgumentException("Invalid type on set;  must be BooleanEO or derivative");
      setValue(((BooleanEO) value).booleanValue());
   }
   
   public boolean isEmpty() { return false; }
   
   public Title title() {  return new Title((booleanValue()) ? ComplexType.localeLookupStatic("yes") : ComplexType.localeLookupStatic("no")); }

   public String toString() { return title().toString(); }
   public String marshal()
   {
      return Boolean.toString(_value);
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof BooleanEO)) return false;
      return _value == ((BooleanEO) obj).booleanValue();
   }

   public int hashCode()
   {
      return _value ? 1231 : 1237;  // from Boolean
   }

   public AtomicRenderer getRenderer() { return vmech().getBooleanRenderer(); }
   public AtomicEditor getEditor() { return vmech().getBooleanEditor(); }

   public void parseValue(String stringValue)
   {
      boolean boolValue = Boolean.valueOf(stringValue).booleanValue();
      setValue(boolValue);
   }

   public EObject makeCopy()
   {
      return new BooleanEO(this.booleanValue());
   }

   // ====
   
   public java.util.List getInequalities()
   {
      return (new IdentityInequality(field())).getInequalities();
   }

}
