package com.u2d.app;

import com.u2d.model.*;
import com.u2d.restrict.Restriction;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.persist.HBMBlock;
import com.u2d.view.EView;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.awt.*;

import org.hibernate.Session;

import javax.swing.*;

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
   private Map<Role, List> _addedRestrictions, _removedRestrictions, _dirtyRestrictions;
   
   public TypeRestrictionMgr(ComplexType type)
   {
      _type = type;
      _roles = hbmPersistor().list(Role.class);

      _addedRestrictions = new HashMap<Role, List>();
      _removedRestrictions = new HashMap<Role, List>();
      _dirtyRestrictions= new HashMap<Role, List>();
   }
   
   public ComplexType getType() { return _type; }
   public AbstractListEO getRoles() { return _roles; }
   
   public void setAddedRestrictionsForRole(Role role, List addedRestrictions)
   {
      _addedRestrictions.put(role, addedRestrictions);
   }
   public void setRemovedRestrictionsForRole(Role role, List removedRestrictions)
   {
      _removedRestrictions.put(role, removedRestrictions);
   }
   public void setDirtyRestrictionsForRole(Role role, List dirtyRestrictions)
   {
      _dirtyRestrictions.put(role, dirtyRestrictions);
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
               List addedRestrictionsForRole = _addedRestrictions.get(role);
               for (int i=0; i<addedRestrictionsForRole.size(); i++)
               {
                  Restriction restriction = (Restriction) addedRestrictionsForRole.get(i);
                  session.save(restriction);
                  role.addRestriction(restriction);
               }
               session.save(role);
            }
            for (Iterator itr = _removedRestrictions.keySet().iterator(); itr.hasNext(); )
            {
               Role role = (Role) itr.next();
               List removedRestrictionsForRole = _removedRestrictions.get(role);
               for (int i=0; i<removedRestrictionsForRole.size(); i++)
               {
                  Restriction restriction = (Restriction) removedRestrictionsForRole.get(i);
                  role.removeRestriction(restriction);
                  session.delete(restriction);
               }
               session.save(role);
            }
            for (Iterator itr = _dirtyRestrictions.keySet().iterator(); itr.hasNext(); )
            {
               Role role = (Role) itr.next();
               List dirtyRestrictionsForRole = _dirtyRestrictions.get(role);
               for (int i=0; i<dirtyRestrictionsForRole.size(); i++)
               {
                  Restriction restriction = (Restriction) dirtyRestrictionsForRole.get(i);
                  session.save(restriction);
               }
               session.save(role);
            }
         }
      });

      return "Changes Applied";
   }


   public EView getMainView()
   {
      EView mgrui = vmech().getTypeRestrictionMgrUi(this);
      _editor = (Editor) mgrui;
      return mgrui;
   }


   public String iconLgResourceRef() { return "images/Restriction32.png"; }

   public Icon iconLg()
   {
      ImageIcon lgIcon = (ImageIcon) super.iconLg();
      ImageIcon smIcon = (ImageIcon) _type.iconSm();
      BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
      Graphics g = bi.getGraphics();
      g.drawImage(lgIcon.getImage(), 0, 0, null);
      g.drawImage(smIcon.getImage(), 16, 16, null);
      return new ImageIcon(bi);
   }

   public String iconSmResourceRef() { return "images/Restriction16.png"; }
   

   public Title title()
   {
      return new Title("Restriction Manager for type '"+_type+"'");
   }
   
}
