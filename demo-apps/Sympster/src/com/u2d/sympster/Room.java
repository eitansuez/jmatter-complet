package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.ColorEO;
import com.u2d.calendar.ScheduleEO;
import javax.persistence.Entity;
import java.awt.Color;

@Entity
public class Room extends ScheduleEO
{
   private final StringEO name = new StringEO();
   private final ColorEO color = new ColorEO();
   
   public static String[] fieldOrder = {"name", "color"};
   public static Color colorCode = new Color(0xf0d526);
   public static String defaultSearchPath = "name";
   
   public Room() {}

   public StringEO getName() { return name; }
   public ColorEO getColor() { return color; }
   
   public Title title() { return name.title(); }

   public Class eventType() { return Session.class; }
}
