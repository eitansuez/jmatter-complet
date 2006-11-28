/*
 * Created on Jan 19, 2004
 */
package com.u2d.domain;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;

/**
 * test class used for testing against framework
 * 
 * @author Eitan Suez
 */
public class Shipment extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO("");
   private final FloatEO _weight = new FloatEO();
   private final USAddress _from = new USAddress();
   private final USAddress _to = new USAddress();
   private Order _order;
   
   public static String[] fieldOrder = {"name", "from", "to", "weight", "order"};
   public static String[] tabViews = {"from", "to"};
   public static String[] identities = {"name"};

   public Shipment() {}
   
   public Shipment(String name, float weight)
   {
      _name.setValue(name);
      _weight.setValue(weight);
   }
   
   public StringEO getName() { return _name; }
   public FloatEO getWeight() { return _weight; }
   public USAddress getFrom() { return _from; }
   public USAddress getTo() { return _to; }
   
   public Order getOrder() { return _order; }
   public void setOrder(Order order)
   {
      Order oldOrder = _order;
      _order = order;
      firePropertyChange("order", oldOrder, _order);
   }
   public void associateOrder(Order order)
   {
      if (order.getShipment() != null)
         order.getShipment().dissociateOrder(null);
      
      order.setShipment(this);
      setOrder(order);
   }
   public void dissociateOrder(Order order)
   {
      if (_order == null) return;
      _order.setShipment(null);
      setOrder(null);
   }
   
   public Title title()
   {
      if (_from == null || _to == null)
         return _name.title().appendParens(_weight).append("lbs");
      
      String fromZip = _from.getZipCode().stringValue();
      String toZip = _to.getZipCode().stringValue();
      return _name.title().appendParens(fromZip + "-" + toZip);
   }
   
   
//   public int validate()
//   {
//      int count = super.validate(); // necessary
//      
//      // test custom rule:  from and to address.city cannot be the same
//      if (!_from.getCity().isEmpty() && 
//            _from.getCity().equals(_to.getCity()))
//      {
//         fireValidationException("Origin and Destination Cities ("+_from.getCity()+") cannot be the same");
//         count++;
//      }
//      else
//      {
//         fireValidationException("");
//      }
//      return count;
//   }


   public void commandHello()
   {
      System.out.println("hello, my name is "+_name+" and i weight "+_weight+" lbs.");
   }
   
   public static void commandSomeStaticShipmentCommand()
   {
      System.out.println("yes, this is indeed a test..");
   }
   
}
