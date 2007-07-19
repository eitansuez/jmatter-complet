/*
 * Created on Feb 23, 2004
 */
package com.u2d.type;

import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;

/**
 * @author Eitan Suez
 */
public class USState extends AbstractChoiceEO
{
   private final StringEO _code = new StringEO();
   private final StringEO _caption = new StringEO();
   public static String[] identities = {"code"};
   
   public USState() {}
   public USState(String code, String caption)
   {
      _code.setValue(code);
      _caption.setValue(caption);
   }
   
   public StringEO getCode() { return _code; }
   public StringEO getCaption() { return _caption; }
   
   public Title title()
   {
      return _caption.title().appendParens(_code);
   }
}
