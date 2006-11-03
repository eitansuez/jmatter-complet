/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.element.EOCommand;

/**
 * @author Eitan Suez
 */
public class CommandRestriction extends Restriction
{
   public static String roleInverseFieldName = "restrictions";

   public static String[] fieldOrder = {"role", "element"};
   
   public CommandRestriction() {}
   
   public CommandRestriction(Role role, EOCommand element)
   {
      super(role,  element);
   }

   public boolean forbidden() { return true; }
}
