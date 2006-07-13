package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.PhotoIconAssistant;
import com.u2d.list.RelationalList;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.CommandAt;

import javax.swing.Icon;
import java.awt.Color;

public class Speaker extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   private final StringEO title = new StringEO();
   private final TextEO bio = new TextEO();
   private final ImgEO photo = new ImgEO();

   private final RelationalList talks = new RelationalList(Talk.class);
   public static Class talksType = Talk.class;
   public static String talksInverseFieldName = "speaker";

   public static String[] fieldOrder = {"name", "title", "photo", "bio", "talks"};
   public static Color colorCode = new Color(0x4169aa);

   public Speaker() {}

   public StringEO getName() { return name; }
   public StringEO getTitle() { return title; }
   public TextEO getBio() { return bio; }
   public ImgEO getPhoto() { return photo; }
   public RelationalList getTalks() { return talks; }

   private transient PhotoIconAssistant assistant = new PhotoIconAssistant(this, photo);
   public Icon iconLg() { return assistant.iconLg(); }
   public Icon iconSm() { return assistant.iconSm(); }

   public Title title() { return name.title(); }
   
   @CommandAt(mnemonic='a')
   public Talk AddTalk(CommandInfo cmdInfo)
   {
      Talk talk = (Talk) createInstance(Talk.class);
      talk.setSpeaker(this);
      getTalks().add(talk);
      return talk;
   }

}
