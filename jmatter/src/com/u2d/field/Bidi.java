/*
 * Created on May 1, 2004
 */
package com.u2d.field;

import com.u2d.element.Field;

/**
 * @author Eitan Suez
 */
public interface Bidi
{
   public void setInverseField(Field inverseField);
   public boolean isBidirectionalRelationship();
}
