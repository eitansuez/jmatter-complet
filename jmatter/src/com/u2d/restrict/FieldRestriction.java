/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.element.Field;

/**
 * @author Eitan Suez
 */
public class FieldRestriction extends Restriction
{
   public static String roleInverseFieldName = "restrictions";
   
   public static final String NONE = "None";
   public static final String READ_ONLY = "ReadOnly";
   public static final String HIDDEN = "Hidden";

   private final FieldRestrictionType _type = new FieldRestrictionType(READ_ONLY);

   public static String[] fieldOrder = { "role", "element", "restrictionType" };
   
   public FieldRestriction() {}
   public FieldRestriction(Role role, Field element)
   {
      super(role, element);
   }

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
