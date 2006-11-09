/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.element.Member;
import com.u2d.element.Command;
import com.u2d.model.EObject;

/**
 * @author Eitan Suez
 */
public class CommandRestriction extends Restriction
{
   protected Command _member;

   public static String roleInverseFieldName = "restrictions";

   public static String[] fieldOrder = {"role", "member"};
   
   public CommandRestriction() {}
   
   public CommandRestriction(Role role)
   {
      super(role);
   }
   
   public CommandRestriction(Role role, Command cmd)
   {
      super(role);
      _member = cmd;
   }

   public void on(Command cmd) { _member = (Command) cmd; }

   public Command getMember() { return _member; }
   public void setMember(Command member)
   {
      Member oldMember = _member;
      _member = member;
      firePropertyChange("member", oldMember, _member);
   }

   public Member member() { return _member; }

   // not sure i like this but the way things are defined right
   // now is that a commandrestriction is binary.  so the mere
   // presence of a commandrestrictions is defined to imply
   // a restriction.  i.e. forbidden=true.  conversely, the absence
   // of a restriction implies command is enabled.
   public boolean forbidden(EObject target) { return true; }
}
