/*
 * Created on Feb 9, 2004
 */
package com.u2d.persist;

/**
 * @author Eitan Suez
 */
public class PersistenceException extends Exception
{
   public PersistenceException(String msg)
   {
      super(msg);
   }
   
   public PersistenceException(Throwable cause)
   {
      super(cause);
   }
}
