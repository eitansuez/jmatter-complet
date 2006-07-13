/*
 * Created on Feb 23, 2004
 */
package com.u2d.type.composite;

import com.u2d.model.ComplexType;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.type.atom.StringEO;

/**
 * @author Eitan Suez
 */
public class ContactMethod extends AbstractChoiceEO
{
   private final StringEO _code = new StringEO();
   private final StringEO _caption = new StringEO();
   public static String[] identities = {"code"};
   
   public ContactMethod() {}
   public ContactMethod(String code, String caption)
   {
      _code.setValue(code);
      _caption.setValue(caption);
   }
   
   public StringEO getCode() { return _code; }
   public StringEO getCaption() { return _caption; }
   
   public ComplexType choiceType() { return type(); }
}
