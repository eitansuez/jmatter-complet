/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.element.CommandInfo;
import com.u2d.find.Searchable;
import com.u2d.find.inequalities.NumericalInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.*;
import com.u2d.reflection.CommandAt;

import java.text.DecimalFormat;

/**
 * @author Eitan Suez
 */
public class FloatEO extends AbstractAtomicEO
                     implements NumericEO, Searchable
{
   private double _value;

   public FloatEO() {}

   public FloatEO(double value)
   {
      _value = value;
   }

   public float floatValue() { return (float) _value; }
   public double doubleValue() { return _value; }
   public void setValue(double value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof FloatEO))
         throw new IllegalArgumentException("Invalid type on set;  must be FloatEO");
      setValue(((FloatEO) value).doubleValue());
   }

   public boolean isEmpty() { return false; }

   private static DecimalFormat format = new DecimalFormat("#.##");
   
   public Title title()
   {
      return new Title(format.format(_value));
   }

   public String toString()
   {
      return title().toString();
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof FloatEO)) return false;
      return _value == ((FloatEO) obj).doubleValue();
   }

   public int hashCode() { return new Double(_value).hashCode(); }


   public AtomicRenderer getRenderer() { return vmech().getFloatRenderer(); }
   public AtomicEditor getEditor() { return vmech().getFloatEditor(); }

   @CommandAt
   public void Double(CommandInfo cmdInfo)
   {
      _value *= 2;
      fireStateChanged();
   }

   public void parseValue(String stringValue)
   {
      double doubleVal = Double.parseDouble(stringValue);
      setValue(doubleVal);
   }

   public EObject makeCopy()
   {
      return new FloatEO(this.doubleValue());
   }

   // =====

   public java.util.List getInequalities()
   {
      return new NumericalInequalities(field()).getInequalities();
   }

}
