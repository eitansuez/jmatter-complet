/*
 * Created on Feb 11, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import com.u2d.domain.*;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;

/**
 * @author Eitan Suez
 */
public class CancelRestoreTest extends TestCase
{
   public void testCancelRestore()
   {
      Shipment shipment = new Shipment("Test Shipment", 25);
      USAddress fromAddr = new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749");
      shipment.getFrom().setValue(fromAddr);
      USAddress toAddr = new USAddress("7301 Ashkelon Blvd", "Boston", "MA", "03221");
      shipment.getTo().setValue(toAddr);
      
      shipment.saveCopy();
      shipment.getName().setValue(new StringEO("Some Other Name"));
      shipment.getWeight().setValue(new FloatEO(12));
      USAddress anotherAddress = new USAddress("123 Some St", "XTown", "TX", "33311");
      shipment.getTo().setValue(anotherAddress);
      
      shipment.restoreCopy();
      assertEquals(shipment.getName().stringValue(), "Test Shipment");
      assertEquals(shipment.getWeight().floatValue(), 25, 0.01);
      assertEquals(shipment.getTo().getCity().stringValue(), "Boston");
      
   }
}
