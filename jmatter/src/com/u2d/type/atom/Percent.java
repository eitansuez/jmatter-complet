/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.NumericalInequalities;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;
import java.text.*;
import java.util.List;

/**
 * @author Eitan Suez
 */
public class Percent extends AbstractAtomicEO 
                     implements NumericEO, Searchable
{
   private double _value;
   private static NumberFormat _percentFormat = NumberFormat.getPercentInstance();
   static
   {
      _percentFormat.setMinimumFractionDigits(2);
   }

   public Percent() {}
   
   public Percent(double value)
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
      if (!(value instanceof Percent))
         throw new IllegalArgumentException("Invalid type on set;  must be Percent");
      setValue(((Percent) value).doubleValue());
   }
   
   public boolean isEmpty() { return false; }
   
   public double applyTo(USDollar amt)
   {
      return _value * amt.doubleValue();
   }
   
   public Title title()
   {
      String formattedString = _percentFormat.format(_value);
      return new Title(formattedString);
   }
   public String toString()
   {
      return title().toString();
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Percent)) return false;
      return _value == ((Percent) obj).doubleValue();
   }

   public int hashCode() { return new Double(_value).hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getPercentRenderer(); }
   public AtomicEditor getEditor() { return vmech().getPercentEditor(); }

   public void parseValue(String stringValue) throws java.text.ParseException
   {
      Number number = _percentFormat.parse(stringValue);
      setValue(number.doubleValue());
      //System.err.println("Parse Exception (Percent Format on "+stringValue+"): "+ex.getMessage());
   }

   public EObject makeCopy()
   {
      return new Percent(this.doubleValue());
   }

   // ===
   
   public List getInequalities()
   {
      return new NumericalInequalities(field()).getInequalities();
   }
}
