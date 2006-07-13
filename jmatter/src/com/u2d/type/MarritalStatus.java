/*
 * Created on Feb 23, 2004
 */
package com.u2d.type;

import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;

/**
 * The basic argument for Choices to not be UserType's is that
 * if they're made ComplexEObjects, then they can be exposed
 * automatically by the framework visually.  The persistence of
 * these objects is also managed.
 * 
 * @author Eitan Suez
 */
public class MarritalStatus extends AbstractChoiceEO
{
   private final StringEO _code = new StringEO();
   private final StringEO _caption = new StringEO();
   public static String[] identities = {"code"};
   
   public MarritalStatus() {}
   public MarritalStatus(String code, String caption)
   {
      _code.setValue(code);
      _caption.setValue(caption);
   }
   
   public StringEO getCode() { return _code; }
   public StringEO getCaption() { return _caption; }
   
   public ComplexType choiceType() { return type(); }
}
