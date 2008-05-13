/*
 * Created on Dec 29, 2004
 */
package com.u2d.type.atom;

import java.text.ParseException;
import java.util.*;
import com.u2d.find.Searchable;
import com.u2d.find.inequalities.IdentityInequality;
import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;
import com.u2d.type.Choice;

/**
 * @author Eitan Suez
 */
public abstract class ChoiceEO extends AbstractAtomicEO
                               implements Searchable, Choice
{
   protected String _value = "";
   
   public boolean isEmpty() { return false; }

   public void parseValue(String value) throws java.text.ParseException
   {
      if (StringEO.isEmpty(value))
      {
         _value = "";
         return;
      }
      if (!entries().contains(value))
      {
         String msg = String.format("Invalid value: [%s] (not in set)", value);
         throw new ParseException(msg, 0);
      }
      _value = value;
   }
   
   public String caption() { return _value; }
   public String code() { return _value; }
   public abstract Collection entries();
   
   public Title title() { return new Title(_value); }
   public String toString() { return _value; }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      boolean classesAreSame = obj.getClass().equals(getClass());
      if (!classesAreSame) return false;
      
      String code = ((ChoiceEO) obj).code();
      return _value.equals(code);
   }

   public int hashCode() { return _value.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getChoiceEORenderer(); }
   public AtomicEditor getEditor() { return vmech().getChoiceEOEditor(); }

   public EObject makeCopy()
   {
      try
      {
         ChoiceEO copy = getClass().newInstance();
         copy.setValue(_value);
         return copy;
      }
      catch (InstantiationException ex)
      {
         System.err.println("InstantiationException: "+ex.getMessage());
         return null;
      }
      catch (IllegalAccessException ex)
      {
         System.err.println("IllegalAccessException: "+ex.getMessage());
         return null;
      }
   }
   
   public void setValue(String value)
   {
      _value = value;
      fireStateChanged();
   }

   public void setValue(EObject value)
   {
      if (!(ChoiceEO.class.isAssignableFrom(value.getClass())))
         throw new IllegalArgumentException("Invalid type on set;  must be ChoiceEO or derivative");
      String code = ((ChoiceEO) value).code();
      setValue(code);
   }
   
   // convenience..
   public boolean is(String code)
   {
      return code().equals(code);
   }

   // ===

   public List getInequalities()
   {
      return new IdentityInequality(field()).getInequalities();
   }

   public static Class getCustomTypeImplementorClass()
   {
      return com.u2d.persist.type.ChoiceEOUserType.class;
   }

}
