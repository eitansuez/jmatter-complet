package com.u2d.type.atom;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 12, 2008
 * Time: 2:12:16 PM
 */
public class ParseException extends RuntimeException
{
   public ParseException()
   {
   }

   public ParseException(String message)
   {
      super(message);
   }

   public ParseException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public ParseException(Throwable cause)
   {
      super(cause);
   }
}
