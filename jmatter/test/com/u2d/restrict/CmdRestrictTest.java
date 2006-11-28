package com.u2d.restrict;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 27, 2006
 * Time: 6:40:29 PM
 */

import junit.framework.TestCase;
import com.u2d.app.Role;
import com.u2d.model.ComplexType;
import com.u2d.element.Command;
import com.u2d.type.composite.LoggedEvent;

public class CmdRestrictTest
      extends TestCase
{
   public void testSuperCmd()
   {
      ComplexType creationRestrictionType = ComplexType.forClass(CreationRestriction.class);
      Command creatingCreationRestrictions = creationRestrictionType.command("New");
      
      ComplexType commandRestrictionType = ComplexType.forClass(CommandRestriction.class);
      Command creatingCommandRestrictions = commandRestrictionType.command("New");
      
      Command superCmd = creatingCreationRestrictions.superCmd(creationRestrictionType);
      assertEquals(creatingCommandRestrictions, superCmd);
      assertSame(creatingCommandRestrictions, superCmd);
   }

   public void testCmdRestrictionOnTypeHierarchy()
   {
      Role myRole = new Role("A Role");
      
      // 1. apply a restriction:  cannot create restrictions
      ComplexType restrictionType = ComplexType.forClass(Restriction.class);
      Command createCmd = restrictionType.command("New");
      
      CommandRestriction restriction = new CommandRestriction(myRole);
      myRole.addCmdRestriction(restriction).on(createCmd);
      createCmd.applyRestriction(restriction);
      
      
      // 2a. verify
      assertTrue(createCmd.isForbidden(restrictionType));

      // 2. verify:  is the creation of a subtype "creationrestriction" forbidden?
      ComplexType creationRestrictionType = ComplexType.forClass(CreationRestriction.class);
      Command creatingCreationRestrictions = creationRestrictionType.command("New");
      assertTrue(creatingCreationRestrictions.isForbidden(creationRestrictionType));
   }
   
   public void testCmdRestrictionOnTypeHierarchy2()
   {
      Role myRole = new Role("A Role");

      // 1. forbid the editing of any kind of restriction
      CommandRestriction restriction = new CommandRestriction(myRole);
      Command editRestrictions = restriction.command("Edit");
      myRole.addCmdRestriction(restriction).on(editRestrictions);
      
      myRole.applyRestrictions();
      
      Command editCmd;
      
      // 2a. question:  is the editing of a simple command restriction forbidden?
      // answer: should be true..
      // take a simple command restriction, such as the ability to delete logs..
      Command deleteLogs = ComplexType.forClass(LoggedEvent.class).instanceCommand("Delete");
      assertNotNull(deleteLogs);
      CommandRestriction cmdRestriction = new CommandRestriction(myRole, deleteLogs);
      editCmd = cmdRestriction.command("Edit");
      assertTrue(editCmd.isForbidden(cmdRestriction));
      
      
      // 2b. question:  is the editing of CreationRestrictions forbidden?
      // if type hierarchy is observed, then the answer should be 'yes.'
      
      CreationRestriction creationRestriction = new CreationRestriction(ComplexType.forClass(LoggedEvent.class));
      editCmd = creationRestriction.command("Edit");
      assertTrue(editCmd.isForbidden(creationRestriction));
   }
   
}