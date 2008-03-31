/*
 * Created on Jan 27, 2004
 */
package com.u2d.view.swing;

import com.u2d.app.*;
import com.u2d.domain.*;
import com.u2d.list.SimpleListEO;
import com.u2d.type.composite.*;

/**
 * @author Eitan Suez
 */
public class AppFrameTester
{
   ViewMechanism _vmech;

   public AppFrameTester()
   {
      SwingViewMechanism.getInstance().launch();
      _vmech = SwingViewMechanism.getInstance();
   }

   void testEObjects()
   {
      Shipment shipment = new Shipment("My Shipment", 25);
      USAddress addr = new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749");
      //shipment.setFrom(addr);
      shipment.getFrom().setValue(addr);
      USAddress addr2 = new USAddress("7301 Ashkelon Rd", "Boston", "MA", "08703");
      //shipment.setTo(addr2);
      shipment.getTo().setValue(addr2);

      Shipment sh2 = new Shipment("Second Shipment", 5);
      //sh2.setFrom(addr2);
      sh2.getFrom().setValue(addr2);
      //sh2.setTo(addr);
      sh2.getTo().setValue(addr);

      _vmech.displayView(addr.getFormView(), null);
      _vmech.displayView(shipment.getFormView(), null);
      _vmech.displayView(addr2.getFormView(), null);
      _vmech.displayView(sh2.getFormView(), null);
   }

   void testLists()
   {
      SimpleListEO shipments = shipmentsList();
//      _vmech.displayView(shipments.getView());
      _vmech.displayView(shipments.getListViewAsIcons(), null);

      try {
         Thread.sleep(4000);
      } catch (Exception ex) {}

      shipments.add(new Shipment("My Last Shipment", 23));

      try {
         Thread.sleep(2000);
      } catch (Exception ex) {}

      Shipment shipment = (Shipment) shipments.getElementAt(2);
      shipments.remove(shipment);
   }

   private SimpleListEO shipmentsList()
   {
      SimpleListEO shipments = new SimpleListEO(Shipment.class);

      Shipment shipment = null;
      for (int i=0; i<7; i++)
      {
         shipment = new Shipment("A" + i, i+3);
         shipments.add(shipment);
      }
      return shipments;
   }

   public static void main(String[] args)
   {
      AppFrameTester tester = new AppFrameTester();
      //tester.testEObjects();
      tester.testLists();
   }


}
