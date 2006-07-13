/*
 * Created on Jan 23, 2004
 */
package com.u2d.core;

import junit.framework.TestCase;
import java.util.*;
import com.u2d.domain.*;
import com.u2d.element.Field;
import com.u2d.type.composite.*;
import com.u2d.app.Application;

/**
 * @author Eitan Suez
 */
public class ValidationTest extends TestCase
{
   Shipment _shipment;
   {
      new Application(true);
   }

   protected void setUp() throws Exception
   {
      _shipment = new Shipment("My Books", 10);
   }

   public void testEmpty()
   {
      Iterator itr = _shipment.childFields().iterator();
      Field field;
      while (itr.hasNext())
      {
         field = (Field) itr.next();
         //System.out.println("Field "+field.getName()+" is empty? "+field.isEmpty(_shipment));
         checkEmpty("shipment.from", field, true);
         checkEmpty("shipment.from.city", field, true);
         checkEmpty("shipment.name", field, false);
      }
   }

   private void checkEmpty(String fieldpath, Field field, boolean empty)
   {
      if (fieldpath.equals(field.getPath()))
      {
         if (empty)
            assertTrue(field.isEmpty(_shipment));
         else
            assertFalse(field.isEmpty(_shipment));
      }
   }

   public void testValidateShipment1()
   {
      validateShipment(false);
   }
   public void testValidateShipment2()
   {
      _shipment.getFrom().setValue(new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749"));
      _shipment.getTo().setValue(new USAddress());
      validateShipment(false);
   }
   public void testValidateShipment3()
   {
      _shipment.getFrom().setValue(new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749"));
      _shipment.getTo().setValue(
            new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749"));  // same city - custom validation
      validateShipment(true);
   }
   public void testValidateShipment4()
   {
      _shipment.getFrom().setValue(new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749"));
      _shipment.getTo().setValue(new USAddress("1200 Some St", "Dallas", "TX", "76000"));
      validateShipment(true);
   }
   public void testValidateShipment5()
   {
      _shipment.getFrom().setValue(new USAddress("9300 Axtellon Ct", "Austin", "TX", "")); // zip blank and is required
      _shipment.getTo().setValue(new USAddress("1200 Some St", "Dallas", "TX", "76000")); // valid
      validateShipment(false);  // from zipCode required and empty
   }
   public void testValidateShipment6()
   {
      _shipment.getFrom().setValue(new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749"));
      _shipment.getTo().setValue(new USAddress("1200 Some St", "Dallas", "TX", "")); // required field missing
      validateShipment(false);
   }

   private void validateShipment(boolean valid)
   {
      int errorCount = _shipment.validate();
      if (errorCount == 0 && !valid)
         fail("Should have had a validation error but didn't");
      if (errorCount > 0 && valid)
         fail("Should not have had a validation error but did");
   }
}
