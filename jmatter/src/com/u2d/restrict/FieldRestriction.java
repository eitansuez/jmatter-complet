/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.element.Field;
import com.u2d.element.Member;
import com.u2d.model.Title;

/**
 * @author Eitan Suez
 */
public class FieldRestriction extends Restriction
{
   protected Field _member;

   public static String roleInverseFieldName = "restrictions";
   
   public static final String NONE = "None";
   public static final String READ_ONLY = "ReadOnly";
   public static final String HIDDEN = "Hidden";

   private final FieldRestrictionType _restrictionType = new FieldRestrictionType(READ_ONLY);

   public static String[] fieldOrder = { "role", "member", "restrictionType" };
   
   public FieldRestriction() {}
   public FieldRestriction(Role role)
   {
      super(role);
   }
   public FieldRestriction(Role role, Field field)
   {
      this(role);
      _member = field;
   }
   public FieldRestriction(Role role, Field field, FieldRestrictionType frt)
   {
      this(role, field);
      _restrictionType.setValue(frt.code());
   }
   
   public void on(Field field) { _member = field; }

   public Field getMember() { return _member; }
   public void setMember(Field member)
   {
      Field oldMember = _member;
      _member = member;
      firePropertyChange("member", oldMember, _member);
   }

   /**
    * The issue is that there are two copies/versions for a member:  the one i introspect, + the one
    * that is restored from database.
    * 
    * @return Introspected version of the db-fetched member
    */
   public Member member()
   {
      return Member.forMember(_member);
   }


   public FieldRestrictionType getRestrictionType() { return _restrictionType; }
   
   public boolean readOnly() { return READ_ONLY.equals(_restrictionType.code()); }
   public boolean hidden() { return HIDDEN.equals(_restrictionType.code()); }
   public boolean none() { return NONE.equals(_restrictionType.code()); }


   public Title title()
   {
      if (_member == null || _role == null)
      {
         String text = String.format("Field Restriction (type: %s)", _restrictionType);
         return new Title(text);
      }
      return _member.title().append(",", _role).append(":", _restrictionType);
   }
}
