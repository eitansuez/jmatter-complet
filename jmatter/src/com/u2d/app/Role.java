/*
 * Created on Nov 1, 2004
 */
package com.u2d.app;

import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.restrict.Restriction;
import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.UserRestriction;
import com.u2d.restrict.CreationRestriction;
import com.u2d.type.atom.*;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.element.Member;
import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.pattern.Block;
import com.u2d.reflection.Cmd;
import com.u2d.view.EView;

import javax.swing.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eitan Suez
 */
public class Role extends AbstractComplexEObject implements Authorizer
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
      setTransientState();
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
   
   public void removeCommandRestriction(CommandRestriction restriction)
   {
      _restrictions.remove(restriction);
      restriction.setRole(null);
   }
   
   public CommandRestriction addCmdRestriction()
   {
      return addCmdRestriction(new CommandRestriction(this));
   }
   
   @Cmd(mnemonic='a')
   public void AddCmdRestriction(CommandInfo cmdInfo, Command cmd)
   {
      CommandRestriction cmdRestriction = new CommandRestriction(this, cmd);
      _restrictions.add(cmdRestriction);
      Set set = new HashSet();
      set.add(this);  set.add(cmdRestriction);
      hbmPersistor().saveMany(set);
   }
   
   @Cmd
   public RoleTypeRestrictionMgr ManageRestrictionsForType(CommandInfo cmdInfo, final ComplexType type)
         throws InterruptedException, InvocationTargetException
   {
      return new RoleTypeRestrictionMgr(this, type);
   }
   
   
   public Title title() { return _name.title(); }

   public void applyRestrictions()
   {
      tracer().info("Role "+_name+": applying restrictions..("+_restrictions.getSize()+")");
      for (Iterator itr = _restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         // even though Member "merges" retrieved objects with one constructed
         // in memory through introspection, associations are not updated.
         // i cannot seem to find a way to plug into hibernate a mechanism
         // for resolving retrieved objects instead of having it use the no-arg
         // constructor..
         Member.forMember(restriction.member()).applyRestriction(restriction);
      }
   }
   public void liftRestrictions()
   {
      tracer().info("Role "+_name+": lifting restrictions..("+_restrictions.getSize()+")");
      for (Iterator itr = _restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         restriction.member().liftRestriction();
      }
   }
   
   public CommandRestriction restrictionOnCmd(Command cmd)
   {
      for (Iterator itr=_restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         if (restriction.member() == cmd)
         {
            return (CommandRestriction) restriction;
         }
      }
      return null;
   }
   public boolean hasRestrictionOnCmd(Command cmd)
   {
      return (restrictionOnCmd(cmd) != null);
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
   
   public void initializePermissions(HBMPersistenceMechanism hbmPersistor)
   {
      if (defaultRole())
      {
      
         if (_restrictions.isEmpty())
         {
            ComplexType userType = ComplexType.forClass(User.class);
            ComplexType logType = ComplexType.forClass(LoggedEvent.class);
            ComplexType roleType = ComplexType.forClass(Role.class);
            ComplexType restrictionType = ComplexType.forClass(Restriction.class);
            
            addCmdRestriction(new UserRestriction()).on(userType.instanceCommand("ChangePassword"));
            addCmdRestriction(new UserRestriction()).on(userType.instanceCommand("Lock"));
            addCmdRestriction(new UserRestriction()).on(userType.instanceCommand("Edit"));
            addCmdRestriction(new UserRestriction()).on(userType.instanceCommand("Delete"));
            
            addCmdRestriction(new CreationRestriction(roleType));
            addCmdRestriction(new CreationRestriction(restrictionType));
            addCmdRestriction(new CreationRestriction(userType));
            addCmdRestriction(new CreationRestriction(logType));
            
            // 1. disable destruction of logs
            addCmdRestriction().on(logType.instanceCommand("Delete"));
            // 2. disable destruction of roles
            addCmdRestriction().on(roleType.instanceCommand("Delete"));
            // 3. disable destruction of command restrictions
            addCmdRestriction().on(restrictionType.instanceCommand("Delete"));
            
            // disable editing logged events
            addCmdRestriction().on(logType.instanceCommand("Edit"));
            // disable editing roles
            addCmdRestriction().on(roleType.instanceCommand("Edit"));
            // disable editing restrictions
            addCmdRestriction().on(restrictionType.instanceCommand("Edit"));
            
            // disallow manipulation of metadata:  restrict commands on Fields
            ComplexType.forClass(Member.class).commands(ReadState.class).forEach(
                  new Block()
                  {
                     public void each(ComplexEObject ceo)
                     {
                        addCmdRestriction().on((Command) ceo);
                     }
                  }
            );
            // i don't like this..
            Command forbidForRole = 
                  ComplexType.forClass(Command.class).instanceCommand("ForbidForRole");
            addCmdRestriction().on(forbidForRole);
            
            Set items = new HashSet();
            items.addAll(_restrictions.getItems());
            items.add(this);
            hbmPersistor.saveMany(items);
         }
      
         // another issue:  on the visibility of types and instances
         
         // another issue:  won't the editing of associations leak?
         //  the ability to right-click associate for example, or to dnd-associate
         //  does not require entering edit mode.  so even if disable "edit"
         //   need to ensure that other parts of code that go around this:  editing
         //   associations (namely) is vetoed as well.  that is, add code to 
         //   just check for the presence of Edit restrictions on the association's
         //   parent.

      }
      
   }

   public boolean authorizes(User user) { return _users.contains(user); }
}
