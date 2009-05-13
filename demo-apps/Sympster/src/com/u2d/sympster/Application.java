package com.u2d.sympster;

import com.u2d.app.Role;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.element.Command;
import com.u2d.model.ComplexType;
import java.util.Set;
import java.util.HashSet;

/**
 * User: eitan
 * Date: Mar 10, 2008
 */
public class Application extends com.u2d.app.Application
{
   public void postInitialize()
   {
      super.postInitialize();
      contributeToIndex(Talk.class, Speaker.class);
   }

   @Override
   public void initializePermissions()
   {
      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
      Role defaultRole = (Role) hbm.fetch("from Role r where r.name = 'Default'");

      Command classBarEditCmd = ComplexType.forClass(ComplexType.class).command("EditClassBar");
      defaultRole.addCmdRestriction().on(classBarEditCmd);

      Set items = new HashSet();
      items.addAll(defaultRole.getRestrictions().getItems());
      items.add(defaultRole);
      hbm.saveMany(items);
   }
}
