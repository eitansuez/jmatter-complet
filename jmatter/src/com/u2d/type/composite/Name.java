/*
 * Created on Jan 6, 2004
 */
package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Fld;

/**
 * @author Eitan Suez
 */
public class Name extends AbstractComplexEObject
{
   private final StringEO _salutation = new StringEO();  // e.g. "mr" or "dr"
   private final StringEO _first = new StringEO();
   private final CharEO _middleInitial = new CharEO();
   private final StringEO _last = new StringEO();
   private final StringEO _suffix = new StringEO();  // e.g. "jr" or "sr"

   public static String[] fieldOrder =
         {"salutation", "first", "middleInitial", "last", "suffix"};

   public Name() {}

   public Name(String first, String last)
   {
      this("", first, last, ' ');
   }
   public Name(String salutation, String first, String last)
   {
      this(salutation, first, last, ' ');
   }
   public Name(String salutation, String first, String last, char mi)
   {
      _salutation.setValue(salutation);
      _first.setValue(first);
      _last.setValue(last);
      _middleInitial.setValue(mi);
   }

   public StringEO getSalutation() { return _salutation; }
   @Fld(mnemonic='f')
   public StringEO getFirst() { return _first; }
   public CharEO getMiddleInitial() { return _middleInitial; }
   public StringEO getLast() { return _last; }
   public StringEO getSuffix() { return _suffix; }


   public Title lastFirstTitle()
   {
      return _last.title().append(",", _first);
   }

   public static String lastFirstToString(StringEO first, StringEO last)
   {
      return last.title().append(",", first).toString();
   }



   public Title firstLastTitle()
   {
      return _salutation.title().append(_first)
            .append(_middleInitial)
            .append(_last)
            .append(",", _suffix);
   }
   public Title title()
   {
      if (lastFirstDisplayMode)
         return lastFirstTitle();
      else
         return firstLastTitle();
   }

   public String toString() { return title().toString(); }


   private static boolean lastFirstDisplayMode = false;

   @Cmd
   public void ExchangeFirstAndLast(CommandInfo cmdInfo)
   {
      lastFirstDisplayMode = !lastFirstDisplayMode;
      fireStateChanged();
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Name)) return false;
      Name otherName = (Name) obj;
      return _first.equals(otherName.getFirst()) &&
             _middleInitial.equals(otherName.getMiddleInitial()) &&
            _last.equals(otherName.getLast()) &&
            _suffix.equals(otherName.getSuffix());
   }

   public int hashCode()
   {
      return ((((( _first.hashCode() * 31 ) + _middleInitial.hashCode()) * 31)
              + _last.hashCode()) * 31) + _suffix.hashCode();
   }

}
