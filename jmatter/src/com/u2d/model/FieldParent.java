/*
 * Created on Jan 21, 2004
 */
package com.u2d.model;

import java.util.List;
import com.u2d.element.Field;

/**
 * @author Eitan Suez
 */
public interface FieldParent
{
   public String name();
   public Class getJavaClass();
   public FieldParent parent();
   public List fields();
   public Field field(String name);
   public boolean isAbstract();
}
