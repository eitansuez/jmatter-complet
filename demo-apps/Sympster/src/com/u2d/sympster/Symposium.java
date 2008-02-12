package com.u2d.sympster;

import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.USDollar;
import com.u2d.type.atom.BigDecimalEO;
import com.u2d.calendar.CalendarEO;
import com.u2d.persist.Persist;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.element.Field;
import com.u2d.find.QuerySpecification;
import com.u2d.find.FieldPath;
import com.u2d.find.Inequality;
import com.u2d.find.inequalities.IdentityInequality;

import java.awt.Color;

@Persist
public class Symposium extends CalendarEO
{
   private final StringEO name = new StringEO();
   private final USDollar registrationPrice = new USDollar();
   private Venue venue;
   private City city;

   public static Color colorCode = new Color(0x04b144);
   
   public static String[] fieldOrder = {"name", "city", "venue", "registrationPrice"};

   public Symposium() {}

   public StringEO getName() { return name; }
   
   public Venue getVenue() { return venue; }
   public void setVenue(Venue venue)
   {
      Venue oldVenue = this.venue;
      this.venue = venue;
      firePropertyChange("venue", oldVenue, this.venue);
   }
   public QuerySpecification venueOptions()
   {
      if (venue == null || city == null) return null;
      Field cityField = venue.field("city");
      FieldPath fp = new FieldPath(cityField.fullPath());
      Inequality equals = new IdentityInequality(cityField).new Equals();
      return new QuerySpecification(fp, equals, getCity());
   }
   
   public City getCity() { return city; }
   public void setCity(City city)
   {
      City oldCity = this.city;
      this.city = city;
      firePropertyChange("city", oldCity, this.city);
   }
   
   public USDollar getRegistrationPrice() { return registrationPrice; }
   
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
