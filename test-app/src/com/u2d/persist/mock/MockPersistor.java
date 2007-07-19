/*
 * Created on Jan 31, 2004
 */
package com.u2d.persist.mock;

import com.u2d.app.*;
import com.u2d.domain.*;
import java.util.*;
import com.u2d.list.PlainListEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.type.composite.*;

/**
 * would be nice to put this file in the framework itself but it resorts
 * to types specific to this test domain
 * 
 * @author Eitan Suez
 */
public class MockPersistor implements PersistenceMechanism
{

   public void delete(ComplexEObject ceo)
   {
      // do nothing
   }

   public AbstractListEO browse(ComplexType type)
   {
      return list(type);
   }
   public PlainListEObject list(Class clazz)
   {
      if (USAddress.class.isAssignableFrom(clazz))
      {
         USAddress address = null;
         List<USAddress> addresses = new ArrayList<USAddress>();
         for (int i=0; i<5; i++)
         {
            address = new USAddress("940"+i+" Some Ln", "Austin", "TX", i+"3434");
            address.setReadState();
            addresses.add(address);
         }
         return new PlainListEObject(USAddress.class, addresses);
      }
      
      Shipment shipment = null;
      List<Shipment> shipments = new ArrayList<Shipment>();
      for (int i=0; i<10; i++)
      {
         shipment = new Shipment("a"+i, i+3);
         shipment.setReadState();
         shipments.add(shipment);
      }
      return new PlainListEObject(Shipment.class, shipments);
   }
   public PlainListEObject list(ComplexType type)
   {
      return list(type.getJavaClass());
   }

   public ComplexEObject load(Class clazz, Long id)
   {
      USAddress address = new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749");
      if (USAddress.class.isAssignableFrom(clazz))
      {
         return address;
      }
      Shipment shipment = new Shipment("My Shipment", 25);
      //shipment.setFrom(address);
      shipment.getFrom().setValue(address);
      USAddress address2 = new USAddress("9700 Ashkelon Rd", "Boston", "MA", "09234");
      //shipment.setTo(address2);
      shipment.getTo().setValue(address2);
      return shipment;
   }
   
   public ComplexEObject fetchSingle(Class clazz)
   {
      return load(clazz, null);
   }

   public void save(ComplexEObject ceo)
   {
      // noop
      // throw new PersistenceException("I'm Only A Mock");
   }
   
   public void updateAssociation(ComplexEObject one, ComplexEObject two) {}


   public boolean authenticate(String username, String password)
   {
      return "blah".equalsIgnoreCase(password);
   }
   
   public com.u2d.type.Choice lookup (Class clazz, String code)
   {
      return null;
   }

}
