package org.jmatter.example;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;
import com.u2d.type.composite.USAddress;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;

import javax.persistence.Entity;

@Entity
public class Customer extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "address"};

   public Customer() { }

   private final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   private final USAddress address = new USAddress();
   public USAddress getAddress() { return address; }

   @Cmd
   public Cart NewCart(CommandInfo cmdInfo)
   {
      Cart cart = (Cart) ComplexType.forClass(Cart.class).instance();
      cart.setCustomer(this);
      return cart;
   }
   
   public Title title() { return name.title(); }
}
