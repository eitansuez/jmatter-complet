/*
 * Created on Apr 16, 2004
 */
package com.u2d.calendar;

import com.u2d.model.ComplexEObject;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public interface Schedulable extends ComplexEObject
{
   public Class eventType();
   public Schedule schedule();
}
