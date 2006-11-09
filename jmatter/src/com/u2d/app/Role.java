/*
 * Created on Nov 1, 2004
 */
package com.u2d.app;

import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.EObject;
import com.u2d.restrict.Restriction;
import com.u2d.restrict.CommandRestriction;
import com.u2d.type.atom.*;
import java.util.Iterator;

/**
 * @author Eitan Suez
 */
public class Role extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();
   private final RelationalList _users = new RelationalList(User.class);
   public static Class usersType = User.class;
   public static String usersInverseFieldName = "role";
   
   private final RelationalList _restrictions = new RelationalList(Restriction.class);
   public static Class restrictionsType = Restriction.class;
   public static String restrictionsInverseFieldName = "role";
   
   public static String[] fieldOrder = {"name", "users", "restrictions"};
   public static String[] identities = {"name"};

   public Role() {}

   public Role(String name)
   {
      _name.setValue(name);
   }

   public StringEO getName() { return _name; }
   public RelationalList getUsers() { return _users; }
   public RelationalList getRestrictions() { return _restrictions; }
   
   public CommandRestriction addCmdRestriction(CommandRestriction restriction)
   {
      _restrictions.add(restriction);
      return restriction;
   }
   
   public Title title() { return _name.title(); }

   public void applyRestrictions()
   {
      initDefaultRole();
      
      System.out.println("Role "+_name+": applying restrictions..("+_restrictions.getSize()+")");
      for (Iterator itr = _restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         restriction.member().applyRestriction(restriction);
      }
   }
   public void liftRestrictions()
   {
      for (Iterator itr = _restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         restriction.member().liftRestriction();
      }
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (!(obj instanceof Role)) return false;
      Role role = (Role) obj;
      return _name.equals(role.getName());
   }

   public int hashCode()
   {
      return _name.hashCode();
   }

   public boolean defaultRole()
   {
      return "Default".equals(_name.stringValue());
   }
   
   public void initDefaultRole()
   {
      if (!defaultRole()) return;
      
      class UserRestriction extends CommandRestriction
         {
            public boolean forbidden(EObject target)
            {
               tracer().fine("Checking if command: "+member()+" is forbidden for user "+currentUser()+" on target object "+target);
               User user = (User) target;
               return (!user.equals(currentUser()));
            }
         };
      
      User prototype = new User();
      addCmdRestriction(new UserRestriction()).on(prototype.command("ChangePassword"));
      addCmdRestriction(new UserRestriction()).on(prototype.command("Lock"));
      addCmdRestriction(new UserRestriction()).on(prototype.command("Edit"));
      addCmdRestriction(new UserRestriction()).on(prototype.command("Delete"));
   }
   
}
