package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.calendar.ScheduleEO;
import java.awt.Color;

public class Room extends ScheduleEO
{
   private final StringEO name = new StringEO();
   
   public static String[] fieldOrder = {"name"};
   public static Color colorCode = new Color(0xf0d526);
   public static String defaultSearchPath = "name";
   
   public Room() {}

   public StringEO getName() { return name; }

   public Title title() { return name.title(); }

   public Class eventType() { return Session.class; }
}
