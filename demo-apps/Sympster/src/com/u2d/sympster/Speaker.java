package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.type.composite.Contact;
import com.u2d.list.RelationalList;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Fld;
import com.u2d.reflection.Arg;
import javax.swing.Icon;
import javax.persistence.Entity;
import java.awt.Color;

@Entity
public class Speaker extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "title", "photo", "bio", "talks", "dateOfBirth"};
   public static Color colorCode = new Color(0x4169aa);
//   public static String sortBy = "com.u2d.sympster.Speaker#contact.homePhone";
   public static String sortBy = "name";
   public static String[] tabViews = {"contact", "talks"};
   public static String defaultSearchPath = "name";
   public static String defaultFocusField = "title";

   public static String[] identities = {"name"};

   public Speaker() {}

   private final StringEO name = new StringEO();
   @Fld(displaysize=12)
   public StringEO getName() { return name; }

   private final StringEO title = new StringEO();
   @Fld(displaysize=30)
   public StringEO getTitle() { return title; }

   private final ImgEO photo = new ImgEO();
   public ImgEO getPhoto() { return photo; }
   public RelationalList getTalks() { return talks; }

   private final TextEO bio = new TextEO();
   public TextEO getBio() { return bio; }

   private final DateEO dateOfBirth = new DateEO();
   public DateEO getDateOfBirth() { return dateOfBirth; }

   private final RelationalList talks = new RelationalList(Talk.class);
   public static Class talksType = Talk.class;
   public static String talksInverseFieldName = "speaker";

   private final Contact contact = new Contact();
   public Contact getContact() { return contact; }

   private transient PhotoIconAssistant assistant = new PhotoIconAssistant(this, photo);
   public Icon iconLg() { return assistant.iconLg(); }
   public Icon iconSm() { return assistant.iconSm(); }

   public Title title() { return name.title(); }
   
   @Cmd(mnemonic='a')
   public Talk AddTalk(CommandInfo cmdInfo)
   {
      Talk talk = (Talk) createInstance(Talk.class);
      talk.association("speaker").set(this);
      return talk;
   }

   @Cmd
   public static Talk AddTalkForSpeaker(CommandInfo cmdInfo, @Arg(value="Speaker", valueIsLookupkey=true) Speaker speaker)
   {
      return speaker.AddTalk(cmdInfo);
   }

//   public boolean hasCustomMainTabPanel() { return true; }
//   public EView mainTabPanel() { return new SpeakerPanel(this); }
}
