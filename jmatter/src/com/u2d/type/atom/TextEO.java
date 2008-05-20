/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.*;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;

/**
 * @author Eitan Suez
 */
public class TextEO extends AbstractAtomicEO implements Searchable
{
   private transient boolean _wraps = true;

   private String _value;
   private boolean _brief;

   public TextEO()
   {
      _value = "";
   }
   public TextEO(String value)
   {
      _value = value;
   }
   public TextEO(boolean brief)
   {
      this();
      setBrief(brief);
   }

   public boolean isBrief() { return _brief; }
   public void setBrief(boolean brief)
   {
      _brief = brief;
   }

   public String stringValue() { return _value; }
   public void setValue(String value)
   {
      if (_value.equals(value)) return;
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (value == null)
      {
         setValue("");
         return;
      }
      if (!(value instanceof TextEO))
         throw new IllegalArgumentException("Invalid type on set;  must be TextEO");
      setValue(((TextEO) value).stringValue());
   }

   public Title title() { return new Title(_value); }
   public String toString() { return title().toString(); }

   public boolean isEmpty()
   {
      return (_value == null) || ("".equals(_value.trim()));
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof TextEO)) return false;
      return _value.equals(((TextEO) obj).stringValue());
   }

   public int hashCode() { return _value.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getTextRenderer(); }
   public AtomicEditor getEditor() { return vmech().getTextEditor(); }

   public void parseValue(String stringValue)
   {
      if (stringValue == null) stringValue = "";
      setValue(stringValue.trim());
   }

   public EObject makeCopy()
   {
      return new TextEO(this.stringValue());
   }

   // =====

   public java.util.List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }

   // =====

   public boolean wraps() { return _wraps; }

   /* ** Commands ** */

   @Cmd
   public void ToggleWrapping(CommandInfo cmdInfo)
   {
      _wraps = !_wraps;
      fireStateChanged();
   }

}
