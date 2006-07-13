/*
 * Created on Jan 31, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import com.u2d.domain.*;
import com.u2d.model.ComplexType;
import com.u2d.model.Harvester;
import com.u2d.pattern.*;

import java.util.Iterator;

/**
 * @author Eitan Suez
 */
public class ReflectionTest extends TestCase
{
   Shipment _shipment;

   protected void setUp() throws Exception
   {
      ComplexType shipmentType = ComplexType.forClass(Shipment.class);
      Harvester.harvestCommands(Shipment.class, shipmentType);
      _shipment = new Shipment("My Shipment", 25);
   }
   
   public void testHarvestCommandsTransient()
   {
      _shipment.setTransientState();
      Onion commands = _shipment.commands();
      // this is too fragile:  add a command to superclass to break it
      System.out.println("in transient state, commands are:");
      Iterator itr = commands.iterator();
      while (itr.hasNext())
      {
         System.out.println(itr.next());
      }

      assertEquals("should have 4 commands in transient state", 5, commands.size());
//      Command saveCmd = (Command) commands.get(0);
//      Command cancelCmd = (Command) commands.get(1);
//      assertEquals("command name incorrect", saveCmd.getName(), "Save");
//      assertEquals("command name incorrect", cancelCmd.getName(), "Cancel");
   }

   public void testHarvestCommandsEdit()
   {
      _shipment.setEditState();
      Onion commands = _shipment.commands();
      // this is too fragile:  add  a command to superclass to break it

      System.out.println("in edit state, commands are:");
      Iterator itr = commands.iterator();
      while (itr.hasNext())
      {
         System.out.println(itr.next());
      }

      assertEquals("should have 4 commands in edit state", 5, commands.size());
//      Command saveCmd = (Command) commands.get(0);
//      Command cancelCmd = (Command) commands.get(1);
//      assertEquals("command name incorrect", saveCmd.getName(), "Save");
//      assertEquals("command name incorrect", cancelCmd.getName(), "Cancel");
   }

   public void testHarvestCommandsRead()
   {
      _shipment.setReadState();
      Onion commands = _shipment.commands();

      System.out.println("in read state, commands are:");
      Iterator itr = commands.iterator();
      while (itr.hasNext())
      {
         System.out.println(itr.next());
      }

      assertEquals("should have 6 commands in read state", 6, commands.size());
//      Command editCmd = (Command) commands.get(0);
//      Command deleteCmd = (Command) commands.get(1);
//      Command helloCmd = (Command) commands.get(2);
//      assertEquals("command name incorrect", editCmd.getName(), "Edit");
//      assertEquals("command name incorrect", deleteCmd.getName(), "Delete");
//      assertEquals("command name incorrect", helloCmd.getName(), "Hello");
   }

}
