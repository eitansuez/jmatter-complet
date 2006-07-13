/*
 * Created on Apr 1, 2004
 */
package com.u2d.validation;

/**
 * @author Eitan Suez
 */
public class Required
{
   private boolean _isit;
   private String _msg = "Required field empty";
   
   public Required(boolean isit)
   {
      _isit = isit;
   }
   public Required(boolean isit, String msg)
   {
      _isit = isit;
      _msg = msg;
   }
   
   public boolean isit() { return _isit; }
   public String getMsg() { return _msg; }
}
