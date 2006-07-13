/*
 * Created on Mar 10, 2004
 */
package com.u2d.validation;

/**
 * @author Eitan Suez
 */
public interface ValidationNotifier
{
   public void addValidationListener(ValidationListener l);
   public void removeValidationListener(ValidationListener l);
   public void fireValidationException(String errorMsg);
   public void fireValidationException(String errorMsg, boolean statusType);
}
