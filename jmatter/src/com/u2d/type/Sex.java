/*
 * Created on Feb 23, 2004
 */
package com.u2d.type;

import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;

/**
 * @author Eitan Suez
 */
public class Sex extends AbstractChoiceEO
{
   private final StringEO _code = new StringEO();
   private final StringEO _caption = new StringEO();
   public static String[] identities = {"code"};
   
   public Sex() {}
   public Sex(String code, String caption)
   {
      _code.setValue(code);
      _caption.setValue(caption);
   }
   
   public StringEO getCode() { return _code; }
   public StringEO getCaption() { return _caption; }
   
   public ComplexType choiceType() { return type(); }

   public static String pluralName() { return "Sexes"; }
   
   public boolean isMale()    { return equals(get("m")); }
   public boolean isFemale() { return equals(get("f")); }

}
