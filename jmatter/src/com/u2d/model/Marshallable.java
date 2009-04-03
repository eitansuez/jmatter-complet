package com.u2d.model;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Jul 24, 2008
 * Time: 9:46:32 AM
 */
public interface Marshallable
{
   public String marshal();
   /**
    * omits the validation check
    * @param stringValue the text to be unmarshalled
    */
   public void unmarshal(String stringValue) throws java.text.ParseException;
}
