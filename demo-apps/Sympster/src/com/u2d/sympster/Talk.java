package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.reflection.Fld;
import com.u2d.persist.PKField;
import com.u2d.persist.PKGen;
import com.u2d.persist.PKGenStrategy;
import javax.persistence.Entity;
import java.awt.Color;

/*
Implemented in this way to illustrate how to allow for a table with a natural primary key (legacy)
 in a JMatter application.
 */
@Entity
//@PKField("title")
//@PKGen(strategy= PKGenStrategy.ASSIGNED)
public class Talk extends AbstractComplexEObject implements Event
{
   private final StringEO title = new StringEO();
   private final TextEO talkAbstract = new TextEO();
   private Speaker speaker;
   public static String speakerInverseFieldName = "talks";

   public static String[] fieldOrder = {"title", "talkAbstract", "speaker"};
   public static Color colorCode = new Color(0xff3333);
//   public static String sortBy = "speaker";  // not yet working..
   public static String sortBy = "title";
   public static String defaultSearchpath = "title";

   public Talk() {}

   @Fld(displaysize=25)
   public StringEO getTitle() { return title; }
   public TextEO getTalkAbstract() { return talkAbstract; }
   public Speaker getSpeaker() { return speaker; }
   public void setSpeaker(Speaker speaker)
   {
      Speaker oldSpeaker = this.speaker;
      this.speaker = speaker;
      firePropertyChange("speaker", oldSpeaker, this.speaker);
   }

   public Title title() { return title.title(); }
}
