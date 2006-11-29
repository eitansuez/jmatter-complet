/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.element.CommandInfo;
import com.u2d.find.Searchable;
import com.u2d.find.inequalities.NumericalInequalities;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.reflection.Cmd;

/**
 * @author Eitan Suez
 */
public class LongEO extends AbstractAtomicEO implements NumericEO, Searchable
{
   private long _value;

   public LongEO() {}

   public LongEO(long value)
   {
      _value = value;
   }

   public long longValue() { return _value; }
   public void setValue(long value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof LongEO))
         throw new IllegalArgumentException("Invalid type on set;  must be LongEO");
      setValue(((LongEO) value).longValue());
   }

   public boolean isEmpty() { return false; }

   public Title title() {  return new Title(Long.toString(_value)); }
   public String toString() { return title().toString(); }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof LongEO)) return false;
      return _value == ((LongEO) obj).longValue();
   }

   public int hashCode() { return (int) _value; }

   public AtomicRenderer getRenderer() { return vmech().getLongRenderer(); }
   public AtomicEditor getEditor() { return vmech().getLongEditor(); }

   @Cmd
   public void Double(CommandInfo cmdInfo)
   {
      _value *= 2;
      fireStateChanged();
   }

   public void parseValue(String stringValue)
   {
      long longVal = Long.parseLong(stringValue);
      setValue(longVal);
   }

   public EObject makeCopy()
   {
      return new LongEO(this.longValue());
   }

   // =====

   public java.util.List getInequalities()
   {
      return new NumericalInequalities(field()).getInequalities();
   }

}
