/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.element.CommandInfo;
import com.u2d.find.Searchable;
import com.u2d.find.inequalities.NumericalInequalities;
import com.u2d.model.*;
import com.u2d.reflection.Cmd;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * @author Eitan Suez
 */
public class BigDecimalEO extends AbstractAtomicEO
                     implements NumericEO, Searchable
{
   private BigDecimal _value;

   public BigDecimalEO() {}

   public BigDecimalEO(BigDecimal value)
   {
      _value = value;
   }

   public float floatValue() { return _value.floatValue(); }
   public double doubleValue() { return _value.doubleValue(); }
   public void setValue(BigDecimal value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof BigDecimalEO))
         throw new IllegalArgumentException("Invalid type on set;  must be BigDecimalEO");
      setValue(((BigDecimalEO) value).getValue());
   }

   public boolean isEmpty() { return false; }

   private static DecimalFormat format = new DecimalFormat("#.00");

   public Title title()
   {
       if (_value == null) return new Title("empty");
      return new Title(format.format(_value.doubleValue()));
   }

   public String toString()
   {
      return title().toString();
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof BigDecimalEO)) return false;
      return _value.doubleValue() == ((BigDecimalEO) obj).doubleValue();
   }

   public int hashCode() { return _value.hashCode(); }


   public AtomicRenderer getRenderer() { return vmech().getBigDecimalRenderer(); }
   public AtomicEditor getEditor() { return vmech().getBigDecimalEditor(); }

   @Cmd
   public void Double(CommandInfo cmdInfo)
   {
      _value = _value.multiply(new BigDecimal(2));
      fireStateChanged();
   }

   public void parseValue(String stringValue)
   {
      try
      {
    	 BigDecimal doubleVal = new BigDecimal(stringValue.replace(',', '.'));
         setValue(doubleVal);
      }
      catch (NumberFormatException ex)
      {
//         try
//         {
//            double doubleVal = format.parse(stringValue).doubleValue();
//            setValue(doubleVal);
//         }
//         catch (ParseException e)
//         {
            throw new RuntimeException(ex);
//         }
      }
   }

   public EObject makeCopy()
   {
      return new BigDecimalEO(this._value);
   }

   // =====

   public java.util.List getInequalities()
   {
      return new NumericalInequalities(field()).getInequalities();
   }

   public BigDecimal getValue() 
   {
	   return _value;
   }

}
