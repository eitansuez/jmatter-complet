package org.jmatter.j1mgr;

import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TimeSpan;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.TimeInterval;
import com.u2d.persist.Persist;
import com.u2d.calendar.CalEvent;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.app.User;

import java.util.Calendar;

/**
 * comment:  cannot mark abstract because causes an exception when attempting
 * to query talks.  query mechanism sometimes needs to create a prototype of
 * the type being queried.  if it's abstract, i get an instantiationexception..
 * need another way to mark type abstract:  a method.
 */
@Persist
public class Talk extends CalEvent
{
   protected final StringEO topic = new StringEO();
   private final TimeSpan span = new TimeSpan();
   private final TextEO description = new TextEO();
   
   private Speaker speaker;
   public static String speakerInverseFieldName = "talks";
   
   private Room location;

   public static String[] fieldOrder = {"topic", "span", "speaker", "description", "location"};
   public static String[] tabViews = {"description"};
   
   static
   {
      // The ability to query types currently requires the ability to 
      // create a prototype instance.
      // 
      // This is problematic if the type is marked absract.  So for now pseudo-mark it abstract:
      ComplexType.forClass(Talk.class).setAbstract(true);
      // this is useful to the framework so it does not expose the creation of this base type in the ui.
   }
   
   public Talk() { }
   
   public void initialize()
   {
      super.initialize();
      span.setDuration(new TimeInterval(Calendar.MINUTE, 45));
   }

   public StringEO getTopic() { return topic; }
   public TimeSpan getSpan() { return span; }
   public TextEO getDescription() { return description; }

   public Speaker getSpeaker() { return speaker; }
   public void setSpeaker(Speaker speaker)
   {
      Speaker oldSpeaker = this.speaker;
      this.speaker = speaker;
      firePropertyChange("speaker", oldSpeaker, this.speaker);
   }
   
   public Room getLocation() { return location; }
   public void setLocation(Room location)
   {
      Room oldLocation = this.location;
      this.location = location;
      firePropertyChange("location", oldLocation, this.location);
   }
   
   @Cmd(mnemonic='r')
   public String RegisterForThisTalk(CommandInfo cmdInfo)
   {
      User currentUser = currentUser();
      if (!(currentUser instanceof Attendee))
      {
         return "You need to be signed in as an attendee";
      }
      Attendee attendee = ((Attendee) currentUser);
      boolean added = attendee.addToAgenda(this);
      if (added)
         return "Talk "+this+" has been added to your agenda";
      else
         return "You're already registered for this talk";
   }
   

   public Title title() { return topic.title().append(", by", speaker); }
}
