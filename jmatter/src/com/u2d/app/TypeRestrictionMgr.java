package com.u2d.app;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.restrict.CommandRestriction;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.persist.HBMBlock;
import com.u2d.view.EView;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 25, 2007
 * Time: 2:06:55 PM
 */
public class TypeRestrictionMgr
      extends AbstractComplexEObject
{
   private ComplexType _type;
   private AbstractListEO _roles;
   private Map<Role, List<CommandRestriction>> _addedRestrictions, _removedRestrictions;
   
   public TypeRestrictionMgr(ComplexType type)
   {
      _type = type;
      _roles = hbmPersistor().list(Role.class);

      _addedRestrictions = new HashMap<Role, List<CommandRestriction>>();
      _removedRestrictions = new HashMap<Role, List<CommandRestriction>>();
   }
   
   public ComplexType getType() { return _type; }
   public AbstractListEO getRoles() { return _roles; }
   
   public void setAddedRestrictionsForRole(Role role, List<CommandRestriction> addedRestrictions)
   {
      _addedRestrictions.put(role, addedRestrictions);
   }
   public void setRemovedRestrictionsForRole(Role role, List<CommandRestriction> removedRestrictions)
   {
      _removedRestrictions.put(role, removedRestrictions);
   }
   
   @Cmd
   public String ApplyChanges(CommandInfo cmdInfo)
   {
      _editor.transferValue();
      
      hbmPersistor().transaction(new HBMBlock()
      {
         public void invoke(Session session)
         {
            for (Iterator itr = _addedRestrictions.keySet().iterator(); itr.hasNext(); )
            {
               Role role = (Role) itr.next();
               List<CommandRestriction> addedRestrictionsForRole = _addedRestrictions.get(role);
               for (CommandRestriction restriction : addedRestrictionsForRole)
               {
                  session.save(restriction);
                  role.addCmdRestriction(restriction);
               }
               session.save(role);
            }
            for (Iterator itr = _removedRestrictions.keySet().iterator(); itr.hasNext(); )
            {
               Role role = (Role) itr.next();
               List<CommandRestriction> removedRestrictionsForRole = _removedRestrictions.get(role);
               for (CommandRestriction restriction : removedRestrictionsForRole)
               {
                  role.removeCommandRestriction(restriction);
                  session.delete(restriction);
               }
               session.save(role);
            }
         }
      });

      return "Changes Applied";
   }


   public EView getMainView()
   {
      TypeRestrictionMgrUi mgrui = new TypeRestrictionMgrUi(this);;
      _editor = mgrui;
      return mgrui;
   }

   public Title title()
   {
      return new Title("Restriction Manager for type '"+_type+"'");
   }
}
