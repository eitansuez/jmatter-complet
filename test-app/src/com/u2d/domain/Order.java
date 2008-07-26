/*
 * Created on Feb 3, 2004
 */
package com.u2d.domain;

import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;
import javax.persistence.Entity;

/**
 * @author Eitan Suez
 */
@Entity
public class Order extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO("");
   private Shipment _shipment;
   
   private final RelationalList _orderItems = new RelationalList(OrderItem.class);
   public static Class orderItemsType = OrderItem.class;
   
   private final Note _comments = new Note();
   
   public static String[] fieldOrder = {"name", "orderItems", "shipment", "comments"};
   public static String[] identities = {"name"};
   
   public Order() {}
   
   public Order(String name)
   {
      getName().setValue(name);
   }
   
   public StringEO getName() { return _name; }
   
   public Shipment getShipment() { return _shipment; }
   public void setShipment(Shipment shipment)
   {
      Shipment oldShipment = _shipment;
      _shipment = shipment;
      firePropertyChange("shipment", oldShipment, _shipment);
   }
   public void associateShipment(Shipment shipment)
   {
      if (shipment.getOrder() != null)
         shipment.getOrder().dissociateShipment(null);
      
      shipment.setOrder(this);
      setShipment(shipment);
   }
   public void dissociateShipment(Shipment shipment)
   {
      if (_shipment == null) return;
      _shipment.setOrder(null);
      setShipment(null);
   }
   
   public RelationalList getOrderItems()
   {
      	return _orderItems;
   }
   public void addItem(String descr)
   {
      OrderItem item = new OrderItem(descr);
      getOrderItems().add(item);
   }
   
   public Note getComments() { return _comments; }
   
   public Title title() { return _name.title(); }
   
}
