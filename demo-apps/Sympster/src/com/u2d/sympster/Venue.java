package com.u2d.sympster;

import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.list.RelationalList;

public abstract class Venue extends AbstractComplexEObject
{
   protected final StringEO name = new StringEO();
   protected final RelationalList rooms = new RelationalList(Room.class);
   public static Class roomsType = Room.class;
   public static String defaultSearchPath = "name";
   
   public StringEO getName() { return name; }
   public RelationalList getRooms() { return rooms; }

   public Title title() { return name.title(); }
}
