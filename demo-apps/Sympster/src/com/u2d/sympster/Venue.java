package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.list.RelationalList;
import com.u2d.persist.Persist;

@Persist
public abstract class Venue extends AbstractComplexEObject
{
   public static String defaultSearchPath = "name";
   public static String[] fieldOrder = {"name", "city", "rooms"};
   
   protected final StringEO name = new StringEO();
   protected City city;

   protected final RelationalList rooms = new RelationalList(Room.class);
   public static Class roomsType = Room.class;
   
   public StringEO getName() { return name; }
   public RelationalList getRooms() { return rooms; }
   
   public City getCity() { return city; }
   public void setCity(City city)
   {
      City oldCity = this.city;
      this.city = city;
      firePropertyChange("city", oldCity, this.city);
   }

   public Title title() { return name.title(); }
}
