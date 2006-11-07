/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.element.EOCommand;
import com.u2d.element.Member;

/**
 * @author Eitan Suez
 */
public class CommandRestriction extends Restriction
{
   protected EOCommand _element;

   public static String roleInverseFieldName = "restrictions";

   public static String[] fieldOrder = {"role", "element"};
   
   public CommandRestriction() {}
   
   public CommandRestriction(Role role, EOCommand element)
   {
      super(role);
      _element = element;
   }
   
   public EOCommand getElement() { return _element; }
   public void setElement(EOCommand element)
   {
      Member oldElement = _element;
      _element = element;
      firePropertyChange("element", oldElement, _element);
   }

   public Member element() { return _element; }

   public boolean forbidden() { return true; }
}
