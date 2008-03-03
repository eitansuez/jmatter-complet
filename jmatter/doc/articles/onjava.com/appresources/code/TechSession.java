package org.jmatter.j1mgr;

import com.u2d.persist.Persist;
import com.u2d.type.atom.StringEO;

@Persist
public class TechSession extends Talk
{
   private final StringEO code = new StringEO();
   private Track track;
   private TalkLevel level;

   public static String[] identities = {"code"};

   public static String[] fieldOrder = {"code", "topic", "span", "speaker", "description", "location"};
   public static String[] tabViews = {"description"};

   public TechSession()
   {
   }

   public StringEO getCode() { return code; }

   public Track getTrack() { return track; }
   public void setTrack(Track track)
   {
      Track oldTrack = this.track;
      this.track = track;
      firePropertyChange("track", oldTrack, this.track);
   }
   
   public TalkLevel getLevel() { return level; }
   public void setLevel(TalkLevel level)
   {
      TalkLevel oldLevel = this.level;
      this.level = level;
      firePropertyChange("level", oldLevel, this.level);
   }
   
}
