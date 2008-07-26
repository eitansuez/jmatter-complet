package com.u2d.sympster;

import com.u2d.type.composite.USAddress;
import javax.persistence.Entity;

@Entity
public class Hotel extends Venue
{
   public Hotel() {}

   public void initialize()
   {
      super.initialize();
      address.initialize();
   }

   private final USAddress address = new USAddress();
   public USAddress getAddress() { return address; }

}
