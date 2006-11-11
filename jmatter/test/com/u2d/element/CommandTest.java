package com.u2d.element;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 8, 2006
 * Time: 4:27:52 PM
 */

import junit.framework.TestCase;
import com.u2d.model.ComplexType;
import com.u2d.type.composite.Folder;

public class CommandTest
      extends TestCase
{
   ComplexType aType;
   EOCommand editCmd;

   protected void setUp()
         throws Exception
   {
      aType = ComplexType.forClass(Folder.class);
      editCmd = (EOCommand) aType.instance().command("Edit");
   }
   
   public void testQualifiedName()
   {
      assertNotNull(editCmd);
      assertEquals("Edit", editCmd.name());
      assertEquals("Folder.Edit", editCmd.qualifiedName());
      String fullPath = "com.u2d.type.composite.Folder#Edit";
      assertEquals(fullPath, editCmd.getFullPath());
      
      Command cmd = Command.forPath(fullPath);
      assertEquals(cmd, editCmd);
   }
}