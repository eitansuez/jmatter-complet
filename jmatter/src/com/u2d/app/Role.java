/*
 * Created on Nov 1, 2004
 */
package com.u2d.app;

import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.restrict.Restriction;
import com.u2d.restrict.CommandRestriction;
import com.u2d.type.atom.*;
import com.u2d.type.composite.LoggedEvent;
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
      initializePermissions();
      
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
   
   public void initializePermissions()
   {
      if (defaultRole())
      {
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
      
         // todo:  add here a litany of other restrictions
         //  - on the manipulation of existing roles
         //  - on the manipulation of existing restrictions
         //  - on the manipulation of log information
         
         //  - on the manipulation of metadata (once revise jmatter to
         //     store metadata in the db)
         
         // another issue:  on the visibility of types and instances
         
         // notes:  destruction:  controlled without adding new concepts:
         //    restrictions on command: delete
         
         //   creation:  controlled without adding new concepts:
         //     restrictions on type's New command (and other constructor
         //     commands)
         
         //  - on the creation and destruction of roles
         //  - on the creation and destruction of restrictions
         //  - on the creation and destruction of users
         //  - on the creation and destruction of logs
         class CreationRestriction extends CommandRestriction
         {
            ComplexType _type;

            public CreationRestriction(ComplexType type)
            {
               _type = type;
               _member = _type.command("New");
            }
         };
         
         addCmdRestriction(new CreationRestriction(ComplexType.forClass(Role.class)));
         addCmdRestriction(new CreationRestriction(ComplexType.forClass(Restriction.class)));
         addCmdRestriction(new CreationRestriction(ComplexType.forClass(User.class)));
         addCmdRestriction(new CreationRestriction(ComplexType.forClass(LoggedEvent.class)));

         // bug:  unlike with the previous case where we had to override
         // forbidden(), in this case, should be able to persist this
         // restriction and source it from the db instead of special-casing
         // it in code like in the above.  revise this accordingly: create
         // restrictions and save them to database and remove them from here.
         // possibly do this even in xml.
         
         // bug: classbar and contextmenus for types are constructed once.
         // so if login as admin and then as another user, the contextmenu
         // will show restricted New commands.  todo: fix.
         
         //  thought:  would be nice if commands were rich enough where
         //    i could ask it:  isMutator? (does it change the state of the
         //    underlying object in question).  this way, i could do something
         //    like:  for each mutating command on given type: forbid.

      }
      
   }
   
}
