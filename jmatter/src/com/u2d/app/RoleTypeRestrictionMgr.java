package com.u2d.app;

import com.u2d.model.ComplexType;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.element.CommandInfo;
import com.u2d.persist.HBMBlock;
import com.u2d.restrict.CommandRestriction;
import com.u2d.reflection.Cmd;
import com.u2d.view.EView;
import java.util.List;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 25, 2007
 * Time: 2:06:55 PM
 */
public class RoleTypeRestrictionMgr extends AbstractComplexEObject
{
   private Role _role;
   private ComplexType _type;
   private List<CommandRestriction> _addedRestrictions, _removedRestrictions;
   
   public RoleTypeRestrictionMgr(Role role, ComplexType type)
   {
      _role = role;  _type = type;
   }
   
   public Role getRole() { return _role; }
   public ComplexType getType() { return _type; }
   
   public void setAddedRestrictions(List<CommandRestriction> addedRestrictions)
   {
      _addedRestrictions = addedRestrictions;
   }
   public void setRemovedRestrictions(List<CommandRestriction> removedRestrictions)
   {
      _removedRestrictions = removedRestrictions;
   }
   
   @Cmd
   public String ApplyChanges(CommandInfo cmdInfo)
   {
      _editor.transferValue();
      
      HBMPersistenceMechanism p = Context.getInstance().hbmpersitor();
      p.transaction(new HBMBlock()
      {
         public void invoke(Session session)
         {
            for (CommandRestriction restriction : _addedRestrictions)
            {
               session.save(restriction);
               _role.addCmdRestriction(restriction);
            }
            for (CommandRestriction restriction : _removedRestrictions)
            {
               _role.removeCommandRestriction(restriction);
               session.delete(restriction);
            }
            session.save(_role);
         }
      });

      return "Changes Applied";
   }


   public EView getMainView()
   {
      RoleTypeRestrictionMgrUi mgrui = new RoleTypeRestrictionMgrUi(this);;
      _editor = mgrui;
      return mgrui;
   }

   public Title title()
   {
      return new Title("Restriction Manager for role '"+_role+"' on type '"+_type+"'");
   }
}
