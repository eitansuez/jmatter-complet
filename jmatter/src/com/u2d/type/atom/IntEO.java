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
public class IntEO extends AbstractAtomicEO implements NumericEO, Searchable
{
   private int _value;

   public IntEO() {}

   public IntEO(int value)
   {
      _value = value;
   }

   public int intValue() { return _value; }
   public void setValue(int value)
   {
      if (_value == value) return;
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof IntEO))
         throw new IllegalArgumentException("Invalid type on set;  must be IntEO");
      setValue(((IntEO) value).intValue());
   }

   public boolean isEmpty() { return false; }

   public Title title() {  return new Title(Integer.toString(_value)); }
   public String toString() { return title().toString(); }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof IntEO)) return false;
      return _value == ((IntEO) obj).intValue();
   }

   public int hashCode() { return _value; }

   public AtomicRenderer getRenderer() { return vmech().getIntRenderer(); }
   public AtomicEditor getEditor() { return vmech().getIntEditor(); }

   public IntEO add(IntEO other)
   {
      return new IntEO(_value + other.intValue());
   }
   public void increment() { setValue(_value + 1); }

   @Cmd
   public void Double(CommandInfo cmdInfo)
   {
      _value *= 2;
      fireStateChanged();
   }

   public void parseValue(String stringValue)
   {
      int intVal = Integer.parseInt(stringValue);
      setValue(intVal);
   }

   public EObject makeCopy()
   {
      return new IntEO(this.intValue());
   }

   // =====

   public java.util.List getInequalities()
   {
      return new NumericalInequalities(field()).getInequalities();
   }

}
