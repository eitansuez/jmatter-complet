package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.PhotoIconAssistant;
import com.u2d.type.composite.Contact;
import com.u2d.list.RelationalList;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Fld;
import com.u2d.reflection.Arg;
import com.u2d.view.EView;
import javax.swing.Icon;
import javax.persistence.Entity;
import java.awt.Color;

@Entity
public class Speaker extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   private final StringEO title = new StringEO();
   private final TextEO bio = new TextEO();
   private final ImgEO photo = new ImgEO();
   
   private final Contact contact = new Contact();

   private final RelationalList talks = new RelationalList(Talk.class);
   public static Class talksType = Talk.class;
   public static String talksInverseFieldName = "speaker";

   public static String[] fieldOrder = {"name", "title", "photo", "bio", "talks"};
   public static Color colorCode = new Color(0x4169aa);
//   public static String sortBy = "com.u2d.sympster.Speaker#contact.homePhone";
   public static String sortBy = "name";
   public static String[] tabViews = {"contact", "talks"};
   public static String defaultSearchPath = "name";

   public Speaker() {}

   @Fld(displaysize=12)
   public StringEO getName() { return name; }
   @Fld(displaysize=30)
   public StringEO getTitle() { return title; }
   public TextEO getBio() { return bio; }
   public ImgEO getPhoto() { return photo; }
   public RelationalList getTalks() { return talks; }
   
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
   public static Talk AddTalkForSpeaker(CommandInfo cmdInfo, @Arg(value = "speaker", valueIsLookupkey = true) Speaker speaker)
   {
      return speaker.AddTalk(cmdInfo);
   }


   public boolean hasCustomMainTabPanel() { return true; }
   public EView mainTabPanel() { return new SpeakerPanel(this); }
}
