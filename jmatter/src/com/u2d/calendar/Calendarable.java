/*
 * Created on Mar 4, 2005
 */
package com.u2d.calendar;

import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;

/**
 * @author Eitan Suez
 */
public interface Calendarable extends ComplexEObject
{
   public AbstractListEO schedulables();
   public Class defaultCalEventType();
   public Calendrier calendar();
}
