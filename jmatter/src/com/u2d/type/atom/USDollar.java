/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.NumericalInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.model.*;
import java.text.*;

/**
 * @author Eitan Suez
 */
public class USDollar extends AbstractAtomicEO 
                      implements NumericEO, Searchable
{
   private double _value;
   private static NumberFormat _currencyFormat = NumberFormat.getCurrencyInstance();
   
   public USDollar() {}
   
   public USDollar(double value)
   {
      _value = value;
   }
   
   public USDollar add(double amount)
   {
      setValue(_value + amount);
      return this;  // for chaining
   }
   public USDollar add(USDollar amount)
   {
      return add(amount.doubleValue());
   }

   private static double TOLERANCE = 0.001;
   public boolean isSameAs(USDollar amount)
   {
      return (_value - amount.doubleValue() < TOLERANCE);
   }

   public float floatValue() { return (float) _value; }
   public double doubleValue() { return _value; }
   public void setValue(double value)
   {
      if (_value == value) return;
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof USDollar))
         throw new IllegalArgumentException("Invalid type on set;  must be USDollar");
      setValue(((USDollar) value).doubleValue());
   }
   
   public boolean isEmpty() { return false; }
   
   public Title title()
   {
      String formattedString = _currencyFormat.format(_value);
      return new Title(formattedString);
   }
   public String toString() { return title().toString(); }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof USDollar)) return false;
      return _value == ((USDollar) obj).doubleValue();
   }

   public int hashCode()
   {
      return new Double(_value).hashCode();
   }

   public AtomicRenderer getRenderer() { return vmech().getUSDollarRenderer(); }
   public AtomicEditor getEditor() { return vmech().getUSDollarEditor(); }
   
   // =====

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      Number number = _currencyFormat.parse(stringValue);
      setValue(number.doubleValue());
      //System.err.println("Parse Exception (Currency Format on "+stringValue+"): "+ex.getMessage());
   }

   public EObject makeCopy()
   {
      return new USDollar(this.doubleValue());
   }

   // =====
   
   public java.util.List getInequalities()
   {
      return new NumericalInequalities(field()).getInequalities();
   }

}
