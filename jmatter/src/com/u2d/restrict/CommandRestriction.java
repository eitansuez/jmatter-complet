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
   public static String roleInverseFieldName = "cmdRestrictions";
   private EOCommand _element;

   public static String[] fieldOrder = {"role", "element"};
   
   public CommandRestriction() {}
   
   public CommandRestriction(Role role, EOCommand element)
   {
      _role = role;
      _element = element;
   }

   public EOCommand getElement() { return _element; }
   public void setElement(EOCommand element)
   {
      EOCommand oldElement = _element;
      _element = element;
      firePropertyChange("element", oldElement, _element);
   }
   
   public Restrictable element() { return _element; }

}
