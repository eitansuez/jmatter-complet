package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;
import com.u2d.list.RelationalList;
import javax.persistence.Entity;

/*
 * comment:  cannot mark abstract because causes an exception when attempting
 * to query venues.  query mechanism sometimes needs to create a prototype of
 * the type being queried.  if it's abstract, i get an instantiationexception..
 * need another way to mark type abstract, as shown below..
 */
@Entity
public class Venue extends AbstractComplexEObject
{
   public static String defaultSearchPath = "name";
   public static String[] fieldOrder = {"name", "city", "rooms"};

   static
   {
      ComplexType.forClass(Venue.class).setAbstract(true);
   }
   
   protected final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   protected City city;
   public City getCity() { return city; }
   public void setCity(City city)
   {
      City oldCity = this.city;
      this.city = city;
      firePropertyChange("city", oldCity, this.city);
   }

   protected final RelationalList rooms = new RelationalList(Room.class);
   public static Class roomsType = Room.class;
   public RelationalList getRooms() { return rooms; }

   public Title title() { return name.title(); }
}
