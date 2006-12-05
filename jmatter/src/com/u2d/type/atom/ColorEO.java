package com.u2d.type.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.model.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 4, 2006
 * Time: 8:57:01 PM
 */
public class ColorEO extends AbstractAtomicEO
{
   private Color _value;
   private static Color defaultValue = Color.white;
   
   public ColorEO() { _value = defaultValue; }
   public ColorEO(Color color) { _value = color; }

   public ColorEO(String hexValue)
   {
      parseValue(hexValue);
   }

   public Color colorValue() { return _value; }
   public void setValue(Color value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (value == null)
      {
         setValue(defaultValue);
         return;
      }
      if (!(value instanceof ColorEO))
         throw new IllegalArgumentException("Invalid type on set;  must be ColorEO");
      setValue(((ColorEO) value).colorValue());
   }
   
   public Title title() { return new Title(_value.toString()); }
   public String toString() { return _value.toString(); }
   
   public boolean isEmpty() { return false; }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof ColorEO)) return false;
      return _value.equals(((ColorEO) obj).colorValue());
   }

   public int hashCode() { return _value.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getColorRenderer(); }
   public AtomicEditor getEditor() { return vmech().getColorEditor(); }

   /* ** Commands ** */
   
   // TODO: add/expose commands for lightening/darkening and 
   //  other algorithms that produce derivative colors
   
   public void parseValue(String stringValue)
   {
      int rgb = (int) Long.parseLong(stringValue, 16);
      _value = new Color(rgb);
   }
   
   public EObject makeCopy()
   {
      return new ColorEO(this.colorValue());
   }

}

