package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.type.atom.TimeSpan;
import com.u2d.calendar.CalEvent;
import com.u2d.view.EView;
import com.u2d.view.swing.CustomReadView;
import com.u2d.view.swing.AlternateView;
import javax.persistence.Entity;
import java.awt.Color;

@Entity
public class Session extends CalEvent
{
   private final TimeSpan time = new TimeSpan();
   private Event event;
   private Room location;
   private Symposium symposium;

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
   public AbstractListEO locationOptions()
   {
      if (symposium == null || symposium.getVenue() == null) return null;
      return symposium.getVenue().getRooms();
   }
   

   public Symposium getSymposium() { return symposium; }
   public void setSymposium(Symposium symposium)
   {
      Symposium oldSymposium = this.symposium;
      this.symposium = symposium;
      firePropertyChange("symposium", oldSymposium, this.symposium);
   }

   public Title title()
   {
      return time.title().append(":", event).append(" in", location);
   }

   public Title calTitle()
   {
      if (event instanceof Talk)
      {
         Talk talk = (Talk) event;
         return talk.title().append(" by", talk.getSpeaker());
      }
      else
      {
         return event.title();
      }
   }

   public static String schedulableFieldname = "location";

   public EView getMainView()
   {
      return new AlternateView(this, new CustomReadView(new SessionView(this)), new String[] {"formview", "omniview"});
   }
}
