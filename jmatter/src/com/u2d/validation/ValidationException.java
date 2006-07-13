/*
 * Created on Sep 8, 2003
 */
package com.u2d.validation;

import com.u2d.model.EObject;


/**
 * @author Eitan Suez
 */
public class ValidationException extends Exception
{
   private EObject _target;
   
   public ValidationException(String msg)
   {
      super(msg);
   }
   
	public ValidationException(String msg, EObject target)
	{
		super(msg);
      _target = target;
	}
   
   public EObject getTarget() { return _target; }
}
