/*
 * Created on Nov 10, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import java.util.*;

import com.u2d.element.Command;
import com.u2d.element.Field;
import com.u2d.model.ComplexType;

/**
 * @author Eitan Suez
 */
public class MetaTest extends TestCase
{
   ComplexType _fieldType, _commandType;

   protected void setUp() throws Exception
   {
      _fieldType = ComplexType.forClass(Field.class);
      _commandType = ComplexType.forClass(Command.class);
   }
   
   public void testFieldCount()
   {
      System.out.println("# of field fields: "+_fieldType.fields().size());
      Iterator itr = _fieldType.fields().iterator();
      Field field = null;
      while (itr.hasNext())
      {
         field = (Field) itr.next();
         System.out.println(field.name());
      }
   }
   
   public void testCommandCount()
   {
      System.out.println("# of cmd fields: "+_commandType.fields().size());
      Iterator itr = _commandType.fields().iterator();
      Field field = null;
      while (itr.hasNext())
      {
         field = (Field) itr.next();
         System.out.println(field.name());
      }
   }

}
