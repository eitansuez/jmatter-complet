package com.u2d.element;

import junit.framework.TestCase;
import com.u2d.model.ComplexType;
import com.u2d.domain.Shipment;
import com.u2d.domain.Painting;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Nov 3, 2008
 * Time: 6:29:18 PM
 */
public class CommandPathTests extends TestCase
{
   public void testCommandPaths()
   {
      ComplexType t = ComplexType.forClass(Shipment.class);
      lookAt(t);
      ComplexType t2 = ComplexType.forClass(Painting.class);
      lookAt(t2);
   }

   private void lookAt(ComplexType t)
   {
      Command ic = t.instanceCommand("Open");
      Command tc = t.command("Open");
      String icpath = ic.fullPath();
      String tcpath = tc.fullPath();
      System.out.println(icpath);
      System.out.println(tcpath);
      Command icrestored = Command.forPath(icpath);
      Command tcrestored = Command.forPath(tcpath);
      System.out.println(icrestored.fullPath());
      System.out.println(tcrestored.fullPath());
      System.out.println("-----");

      Command browse = t.command("Browse");
      Command find = t.command("Find");
      Command newCmd = t.command("New");
      String browsePath = browse.fullPath();
      String findPath = find.fullPath();
      String newCmdPath = newCmd.fullPath();
      System.out.println(browsePath);
      System.out.println(findPath);
      System.out.println(newCmdPath);
   }
}
