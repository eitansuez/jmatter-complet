package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.type.atom.TimeSpan;
import com.u2d.calendar.CalEvent;
import com.u2d.persist.Persist;

import java.awt.Color;

@Persist
public class Session extends CalEvent
{
   private final TimeSpan time = new TimeSpan();
   private Event event;
   private Room location;

   public static String[] fieldOrder = {"event", "time", "location"};
   public static Color colorCode = new Color(0x9966ff);

   public Session() {}

   public TimeSpan getTime() { return time; }

   public Event getEvent() { return event; }
   public void setEvent(Event event)
   {
      Event oldEvent = this.event;
      this.event = event;
      firePropertyChange("event", oldEvent, this.event);
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
      return time.title().append(":", event).append(" in", location);
   }

   public static String schedulableFieldname = "location";
   public Title calTitle()
   {
      if (event == null)
         return new Title("--");
      else
         return event.title();
   }
}
