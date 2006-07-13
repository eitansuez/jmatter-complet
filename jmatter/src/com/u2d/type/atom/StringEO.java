/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.element.CommandInfo;
import com.u2d.find.Searchable;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.model.*;
import com.u2d.reflection.CommandAt;

import java.util.*;

/**
 * @author Eitan Suez
 */
public class StringEO extends AbstractAtomicEO implements Searchable
{
   private String _value;
   
   public StringEO()
   {
      _value = "";
   }
   public StringEO(String value)
   {
      _value = value;
   }
   
   public String stringValue() { return _value; }
   public void setValue(String value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof StringEO))
         throw new IllegalArgumentException("Invalid type on set;  must be StringEO");
      setValue(((StringEO) value).stringValue());
   }
   
   public Title title() { return new Title(_value); }
   public String toString() { return title().toString(); }
   
   public boolean isEmpty()
   {
      return isEmpty(_value);
   }
   public static boolean isEmpty(String value)
   {
      return (value == null) || ("".equals(value.trim()));
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof StringEO)) return false;
      return _value.equals(((StringEO) obj).stringValue());
   }

   public int hashCode() { return _value.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getStringRenderer(); }
   public AtomicEditor getEditor() { return vmech().getStringEditor(); }

   /* ** Commands ** */
   
   @CommandAt
   public void Capitalize(CommandInfo cmdInfo)
   {
      _value = _value.toUpperCase();
      fireStateChanged();
   }
   @CommandAt
   public void Lowercase(CommandInfo cmdInfo)
   {
      _value = _value.toLowerCase();
      fireStateChanged();
   }
   @CommandAt
   public void TitleCase(CommandInfo cmdInfo)
   {
      StringTokenizer tokenizer = new StringTokenizer(_value.toLowerCase(), 
                                                      " \t\n\r\f", 
                                                      true);
      String token;
      StringBuffer result = new StringBuffer();
      while (tokenizer.hasMoreTokens())
      {
         token = tokenizer.nextToken();
         if (Character.isWhitespace(token.charAt(0)))
         {
            result.append(token);
         }
         else
         {
            result.append(Character.toUpperCase(token.charAt(0)))
                  .append(token.substring(1));
         }
      }
      
      _value = result.toString();
      fireStateChanged();
   }
   
   public void parseValue(String stringValue)
   {
      if (stringValue == null) stringValue = "";
      setValue(stringValue.trim());
   }
   
   public EObject makeCopy()
   {
      return new StringEO(this.stringValue());
   }

   
   // =====
   
   /* zero-pad high-order digits */
   public static String zeroPad(int num, int numdigits)
   {
      StringBuffer result = new StringBuffer("");
      int a = 0;
      for (int i=1; i<numdigits; i++)
      {
         a = (int) num / (int) ( Math.pow(10, numdigits - i) );
         if (a == 0)
            result.append("0");
         else
            break;
      }
      result.append(num);
      return result.toString();
   }
   
   // =====
   
   public List getInequalities()
   {
      return new TextualInequalities(field()).getInequalities();
   }

}
