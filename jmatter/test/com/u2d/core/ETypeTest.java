/*
 * Created on Jan 20, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import java.util.Iterator;

import com.u2d.domain.*;
import com.u2d.element.Field;
import com.u2d.model.ComplexType;
import com.u2d.pattern.*;

/**
 * @author Eitan Suez
 */
public class ETypeTest extends TestCase
{
   ComplexType _shipmentType;

   protected void setUp() throws Exception
   {
      _shipmentType = ComplexType.forClass(Shipment.class);
   }
   
   // this may not work anymore..have > 1 command
//   public void testShipmentHelloCommand()
//   {
//      Command command = (Command) _shipmentType.commands().get(0);
//      Shipment shipment = new Shipment("First Shipment", 7);
//      command.execute(shipment);
//      Shipment secondshipment = new Shipment("Second Shipment", 83);
//      command.execute(secondshipment);
//   }

   // commented out:  this test is too fragile.
   // twice i have added a field to the supertype (abstractcomplexeobject)
   // which broke this test (created, then deleted + deletedOn)
   /*
   public void testFieldCount()
   {
      assertEquals(6, _shipmentType.fields().size());
   }
   */

   public void testFieldTypes()
   {
      Iterator itr = _shipmentType.fields().iterator();
      Field field = null;
      while (itr.hasNext())
      {
         field = (Field) itr.next();
//         System.out.println("Field: "+field.getName());
         if ("from".equals(field.getName()) || "to".equals(field.getName()))
         {
            assertTrue(field.isAggregate());
         }
         if ("name".equals(field.getName()) || "weight".equals(field.getName()))
         {
            assertTrue(field.isAtomic());
         }
      }
   }

   public void testFieldsAndPaths()
   {
      System.out.println("\tName\t\tRequired\tIndexed\t\tPath");
      System.out.println("\t--------\t--------\t--------\t--------");
      FieldRecurser.recurseFields(_shipmentType.fields(), new FieldProcessor()
         {
            public void processField(Field field)
            {
               // [a] a visual check
               System.out.println("\t"+field.getName()+"\t\t"+field.required()+"\t\t"+field.isIndexed()+"\t\t"+field.getPath());

               // [b] assertions:
               if ("shipment.from.street1".equals(field.getPath()))
                  assertFalse("shipment.from.street1 should be optional!", field.required());
               if ("shipment.to.street1".equals(field.getPath()))
                  assertTrue("shipment.to.street1 should be required!", field.required());
            }
         });
   }

}
