/*
 * Created on Nov 1, 2004
 */
package com.u2d.app;

import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.element.Field;
import com.u2d.element.Member;
import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.Title;
import com.u2d.pattern.Onion;
import com.u2d.reflection.Cmd;
import com.u2d.restrict.*;
import com.u2d.type.atom.StringEO;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.type.USState;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.HibernateException;

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
   
   
   public Restriction addRestriction(Restriction restriction)
   {
      _restrictions.add(restriction);
      restriction.setRole(this);
      return restriction;
   }
   public CommandRestriction addCmdRestriction(CommandRestriction restriction)
   {
      return (CommandRestriction) addRestriction(restriction);
   }
   
   public void removeRestriction(Restriction restriction)
   {
      _restrictions.remove(restriction);
      restriction.setRole(null);
   }
   public void removeCommandRestriction(CommandRestriction restriction)
   {
      removeRestriction(restriction);
   }
   
   public CommandRestriction addCmdRestriction()
   {
      return addCmdRestriction(new CommandRestriction(this));
   }

   public FieldRestriction addFldRestriction()
   {
      return (FieldRestriction) addRestriction(new FieldRestriction(this));
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
   public RoleTypeRestrictionMgr ManageRestrictionsForType(CommandInfo cmdInfo, ComplexType type)
   {
      return new RoleTypeRestrictionMgr(this, type);
   }
   
   
   public Title title() { return _name.title().append("role"); }

   public void applyRestrictions()
   {
      tracer().info("Role "+_name+": applying restrictions..("+_restrictions.getSize()+")");
      for (Iterator itr = _restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         if (restriction.member() == null)
         {
            throw new RuntimeException("Came across a restriction with a null member! Restriction is: "+restriction);
         }
         restriction.member().applyRestriction(restriction);
      }

      applyFilterRestrictions();
   }

   private boolean isAdmin()
   {
      return "Administrator".equalsIgnoreCase(_name.stringValue());
   }

   public static final String AUTHFILTER_NAME = "authFilter";
   
   private void applyFilterRestrictions()
   {
      if (!isAdmin())
      {
         Session session = hbmPersistor().getSession();
         try
         {
            session.enableFilter(AUTHFILTER_NAME).setParameter("currentUser", currentUser().getID());
         }
         catch (HibernateException ex)
         {
            // it may be that the filter is not even defined in the mappings
            //  in which case a hibernate exception
            //  (such as "org.hibernate.HibernateException: No such filter configured [authFilter]"
            //  is thrown
            // only if an optional authorization-related method is defined on a base type,
            // such as:
            //   public static String typeFilter() { return "technician_id = :currentUser"; }
            // does hbmmaker even define the filter..
         }
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
   
   public Restriction restrictionOnMember(Member member)
   {
      for (Iterator itr=_restrictions.iterator(); itr.hasNext(); )
      {
         Restriction restriction = (Restriction) itr.next();
         if (restriction.member() == member)
         {
            return restriction;
         }
      }
      return null;
   }
   public CommandRestriction restrictionOnCmd(Command cmd)
   {
      return (CommandRestriction) restrictionOnMember(cmd);
   }
   public FieldRestriction restrictionOnFld(Field fld)
   {
      return (FieldRestriction) restrictionOnMember(fld);
   }
   public boolean hasRestrictionOnCmd(Command cmd)
   {
      return (restrictionOnCmd(cmd) != null);
   }
   public boolean hasRestrictionOnFld(Field fld)
   {
      FieldRestriction restriction = restrictionOnFld(fld);
      return (restriction!=null && !restriction.none());
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
      if (_restrictions.isEmpty())
      {
         if (defaultRole())
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
            Onion memberCmds = ComplexType.forClass(Member.class).commands(ReadState.class);
            for (Iterator itr = memberCmds.deepIterator(); itr.hasNext(); )
            {
               addCmdRestriction().on((Command) itr.next());
            }
            // i don't like this..
            Command forbidForRole = 
                  ComplexType.forClass(Command.class).instanceCommand("ForbidForRole");
            addCmdRestriction().on(forbidForRole);
            
            addCmdRestriction().on(roleType.instanceCommand("ManageRestrictionsForType"));
            addCmdRestriction().on(roleType.instanceCommand("AddCmdRestriction"));
            
            for (Iterator itr = ComplexType.persistedTypes().iterator(); itr.hasNext(); )
            {
                  ComplexType type = (ComplexType) itr.next();
                  addCmdRestriction().on(type.command("Open"));
                  addCmdRestriction().on(type.command("ManageRestrictions"));
            }
            ComplexType types = ComplexType.forClass(ComplexType.class);
            addCmdRestriction().on(types.instanceCommand("Open"));
            addCmdRestriction().on(types.instanceCommand("ManageRestrictions"));

            // cannot edit us states:
            ComplexType usstates = ComplexType.forClass(USState.class);
            addCmdRestriction(new CreationRestriction(usstates));
            addCmdRestriction().on(usstates.instanceCommand("Delete"));
            addCmdRestriction().on(usstates.instanceCommand("Edit"));

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


         // give specific application opportunity to programmatically specify a set of
         //  initial restrictions..
         app().initializePermissions();
      }
   }

   public boolean authorizes(User user) { return _users.contains(user); }
}
