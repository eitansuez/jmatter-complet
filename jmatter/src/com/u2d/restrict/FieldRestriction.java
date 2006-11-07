/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.element.Field;
import com.u2d.element.Member;

/**
 * @author Eitan Suez
 */
public class FieldRestriction extends Restriction
{
   protected Field _element;

   public static String roleInverseFieldName = "restrictions";
   
   public static final String NONE = "None";
   public static final String READ_ONLY = "ReadOnly";
   public static final String HIDDEN = "Hidden";

   private final FieldRestrictionType _type = new FieldRestrictionType(READ_ONLY);

   public static String[] fieldOrder = { "role", "element", "restrictionType" };
   
   public FieldRestriction() {}
   public FieldRestriction(Role role, Field element)
   {
      super(role);
      _element = element;
   }
   
   
   public Field getElement() { return _element; }
   public void setElement(Field element)
   {
      Field oldElement = _element;
      _element = element;
      firePropertyChange("element", oldElement, _element);
   }

   public Member element() { return _element; }
   

   public FieldRestrictionType getRestrictionType() { return _type; }
   
   public boolean readOnly()
   {
      return READ_ONLY.equals(_type.code());
   }
   public boolean hidden()
   {
      return HIDDEN.equals(_type.code());
   }
   
}
