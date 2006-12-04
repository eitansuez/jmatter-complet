/*
 * Created on Jan 30, 2004
 */
package com.u2d.type.atom;

import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.model.*;

/**
 * @author Eitan Suez
 */
public class URI extends AbstractAtomicEO implements Searchable
{
   private String _value;
   
   public URI() { _value = ""; }
   public URI(String value) { _value = value; }
   
   public Object getValue() { return _value; }
   public String stringValue() { return _value; }
   
   public void setValue(String value)
   {
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
      if (!(value instanceof URI))
         throw new IllegalArgumentException("Invalid type on set;  must be URI");
      setValue(((URI) value).stringValue());
   }
   
   public boolean isEmpty()
   {
      return (_value == null) || ("".equals(_value.trim()));
   }
   
   public int validate()
   {
      int first = _value.indexOf("://");
      if (first < 1 || first == _value.length() - 1) return invalid();
      String latterpart = _value.substring(first + 3);
      
      //System.out.println("latter part is "+latterpart);

      if (_value.indexOf(" ")!=-1) return invalid();
      
      int second = latterpart.indexOf(".");
      if (second < 1 || second == latterpart.length() - 1) return invalid();
      
      return 0;
   }
   
   private int invalid()
   {
      fireValidationException("Invalid URL address syntax: "+_value);
      return 1;
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof URI)) return false;
      URI uri = (URI) obj;
      return (_value.equals(uri.getValue()));
   }

   public int hashCode() { return _value.hashCode(); }

   public String toString() { return _value; }
   public Title title() {  return new Title(_value); }

   public AtomicRenderer getRenderer() { return vmech().getURIRenderer(); }
   public AtomicEditor getEditor() { return vmech().getURIEditor(); }

   public void parseValue(String stringValue)
   {
      setValue(stringValue);
   }

   public EObject makeCopy()
   {
      return new URI(this.stringValue());
   }

   
   // ===
   
   public java.util.List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }

}
