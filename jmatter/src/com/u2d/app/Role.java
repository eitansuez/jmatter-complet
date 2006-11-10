/*
 * Created on Nov 1, 2004
 */
package com.u2d.app;

import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.restrict.Restriction;
import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.UserRestriction;
import com.u2d.restrict.CreationRestriction;
import com.u2d.type.atom.*;
import com.u2d.type.composite.LoggedEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

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
      restriction.setRole(this);
      return restriction;
   }
   
   public Title title() { return _name.title(); }

   public void applyRestrictions()
   {
      initializePermissions();
      
      tracer().info("Role "+_name+": applying restrictions..("+_restrictions.getSize()+")");
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
      
         if (_restrictions.isEmpty())
         {
            User prototype = new User();
            addCmdRestriction(new UserRestriction()).on(prototype.command("ChangePassword"));
            addCmdRestriction(new UserRestriction()).on(prototype.command("Lock"));
            addCmdRestriction(new UserRestriction()).on(prototype.command("Edit"));
            addCmdRestriction(new UserRestriction()).on(prototype.command("Delete"));
            
            addCmdRestriction(new CreationRestriction(ComplexType.forClass(Role.class)));
            addCmdRestriction(new CreationRestriction(ComplexType.forClass(Restriction.class)));
            addCmdRestriction(new CreationRestriction(ComplexType.forClass(User.class)));
            addCmdRestriction(new CreationRestriction(ComplexType.forClass(LoggedEvent.class)));
            
            // 1. disable destruction of logs
            LoggedEvent evtProto = new LoggedEvent();
            addCmdRestriction(new CommandRestriction(this, evtProto.command("Delete")));
            // 2. disable destruction of roles
            CommandRestriction roleDeletionRestriction = 
                  new CommandRestriction(this, this.command("Delete"));
            // 3. disable destruction of command restrictions
            addCmdRestriction(roleDeletionRestriction);
            addCmdRestriction(new CommandRestriction(this, 
                                                     roleDeletionRestriction.command("Delete")));
            // TODO: how to specify a restriction on a command across a type hierarchy?
            
            Set items = new HashSet();
            items.addAll(_restrictions.getItems());
            items.add(this);
            hbmPersistor().saveMany(items);
         }
      
         // todo:  add here a litany of other restrictions
         //  - on the manipulation of existing roles
         //  - on the manipulation of existing restrictions
         //  - on the manipulation of log information
         
         //  - on the manipulation of metadata (once revise jmatter to
         //     store metadata in the db)
         
         // another issue:  on the visibility of types and instances
         
         //  thought:  would be nice if commands were rich enough where
         //    i could ask it:  isMutator? (does it change the state of the
         //    underlying object in question).  this way, i could do something
         //    like:  for each mutating command on given type: forbid.
         // in ruby, assuming convention can be trusted, can infer from
         //  command name ending with "!"

      }
      
   }
   
}
