/*
 * Created on Feb 9, 2004
 */
package com.u2d.validation;

import java.util.EventListener;

/**
 * @author Eitan Suez
 */
public interface ValidationListener extends EventListener
{
   public void validationException(ValidationEvent evt);
}
