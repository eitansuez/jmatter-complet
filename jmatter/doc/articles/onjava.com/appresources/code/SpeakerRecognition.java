package org.jmatter.j1mgr;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.PhotoIconAssistant;
import com.u2d.persist.Persist;

import javax.swing.*;

@Persist
public class SpeakerRecognition
      extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   private final ImgEO icon = new ImgEO();

   public static String[] fieldOrder = {"name", "icon"};
   
   public SpeakerRecognition()
   {
   }

   public StringEO getName() { return name; }
   public ImgEO getIcon() { return icon; }
   
   private transient PhotoIconAssistant assistant = new PhotoIconAssistant(this, icon);
   public Icon iconLg() { return assistant.iconLg(); }
   public Icon iconSm() { return assistant.iconSm(); }
   

   public Title title()
   {
      return name.title();
   }

}
