/*
 * Created on Feb 9, 2004
 */
package com.u2d.validation;

import java.util.EventObject;

/**
 * @author Eitan Suez
 */
public class ValidationEvent extends EventObject
{
   private String _msg;
   private boolean _statusType = false;
   
   public ValidationEvent(Object source, String msg)
   {
      super(source);
      _msg = msg;
      _statusType = false;
   }
   
   public ValidationEvent(Object source, String msg, boolean statusType)
   {
      super(source);
      _msg = msg;
      _statusType = statusType;
   }

   public String getMsg()
   {
      return _msg;
   }
   
   public boolean isStatusMsg()
   {
      return _statusType;
   }
}
