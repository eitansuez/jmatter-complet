package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.type.atom.TimeSpan;
import com.u2d.calendar.CalEvent;
import java.awt.Color;

public class Session extends CalEvent
{
   private final TimeSpan time = new TimeSpan();
   private Talk talk;
   private Room location;

   public static String[] fieldOrder = {"talk", "time", "location"};
   public static Color colorCode = new Color(0x9966ff);

   public Session() {}

   public TimeSpan getTime() { return time; }

   public Talk getTalk() { return talk; }
   public void setTalk(Talk talk)
   {
      Talk oldTalk = this.talk;
      this.talk = talk;
      firePropertyChange("talk", oldTalk, this.talk);
   }

   public Room getLocation() { return location; }
   public void setLocation(Room location)
   {
      Room oldLocation = this.location;
      this.location = location;
      firePropertyChange("location", oldLocation, this.location);
   }

   public Title title()
   {
      return time.title().append(":", talk).append(" in", location);
   }

   public static String timespanFieldname = "time";
   public static String schedulableFieldname = "location";
   public Title calTitle()
   {
      if (talk == null)
         return new Title("--");
      else
         return talk.title();
   }
}
