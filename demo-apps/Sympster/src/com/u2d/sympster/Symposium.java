package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.type.atom.StringEO;
import com.u2d.calendar.CalendarEO;
import com.u2d.persist.Persist;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;

import java.awt.Color;

@Persist
public class Symposium extends CalendarEO
{
   private final StringEO name = new StringEO();
   private Venue venue;

   public static Color colorCode = new Color(0x04b144);

   public Symposium() {}

   public StringEO getName() { return name; }
   
   public Venue getVenue() { return venue; }
   public void setVenue(Venue venue)
   {
      Venue oldVenue = this.venue;
      this.venue = venue;
      firePropertyChange("venue", oldVenue, this.venue);
   }
   
   public Title title() { return name.title(); }
   public static String pluralName() { return "Symposia"; }
   
   public AbstractListEO schedulables()
   {
      if (venue == null)
      {
         return null;
      }
//      return ComplexType.forClass(Room.class).list();
      return venue.getRooms();
   }

   public Class defaultCalEventType() { return Session.class; }

   @Cmd
   public Object ShowCalendar(CommandInfo cmdInfo)
   {
      if (schedulables() == null || schedulables().isEmpty())
      {
         return "You must first specify a venue with rooms for this symposium";
      }
      return calendar();
   }
}
