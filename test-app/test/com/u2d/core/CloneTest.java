/*
 * Created on Feb 11, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;
import com.u2d.domain.*;

/**
 * @author Eitan Suez
 */
public class CloneTest extends TestCase
{
   
   public void testCopySimpleString()
   {
      StringEO me = new StringEO("Eitan");
      StringEO copy = (StringEO) me.makeCopy();
      assertNotSame(me, copy);
      assertEquals(me, copy);
   }
   
   public void testCopySimpleInt()
   {
      IntEO age = new IntEO(33);
      IntEO copy = (IntEO) age.makeCopy();
      assertNotSame(age, copy);
      assertEquals(age, copy);
   }

   public void testCopySimpleBool()
   {
      BooleanEO value = new BooleanEO(true);
      BooleanEO copy = (BooleanEO) value.makeCopy();
      assertNotSame(value, copy);
      assertEquals(value, copy);
   }
   
   public void testCopySimpleEmail()
   {
      Email value = new Email("blah@u2d.com");
      Email copy = (Email) value.makeCopy();
      assertNotSame(value, copy);
      assertEquals(value, copy);
   }
   
   public void testCopyComplex1()
   {
      Shipment shipment = new Shipment("Test Shipment", 25);
      USAddress fromAddr = new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749");
      shipment.getFrom().setValue(fromAddr);
      USAddress toAddr = new USAddress("7301 Ashkelon Blvd", "Boston", "MA", "03221");
      shipment.getTo().setValue(toAddr);
      
      Shipment copy = (Shipment) shipment.makeCopy();
      assertNotSame(shipment, copy);
      assertEquals(shipment, copy);
      
      assertNotSame(shipment.getFrom(), copy.getFrom());
      assertNotSame(shipment.getTo(), copy.getTo());
   }
   
}
