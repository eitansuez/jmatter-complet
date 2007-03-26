/*
 * Created on Apr 1, 2004
 */
package com.u2d.validation;

import com.u2d.model.ComplexType;

/**
 * @author Eitan Suez
 */
public class Required
{
   private boolean _isit;
   private String _msg = ComplexType.localeLookupStatic("validation.required");
   
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
