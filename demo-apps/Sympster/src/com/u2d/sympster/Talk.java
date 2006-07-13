package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import java.awt.Color;

public class Talk extends AbstractComplexEObject
{
   private final StringEO title = new StringEO();
   private final TextEO talkAbstract = new TextEO();
   private Speaker speaker;
   public static String speakerInverseFieldName = "talks";

   public static String[] fieldOrder = {"title", "talkAbstract", "speaker"};
   public static Color colorCode = new Color(0xff3333);

   public Talk() {}

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
