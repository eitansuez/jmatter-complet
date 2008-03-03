package org.jmatter.j1mgr;

import com.u2d.model.*;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.PhotoIconAssistant;
import com.u2d.persist.Persist;
import com.u2d.list.RelationalList;
import com.u2d.view.FieldViewHandler;
import com.u2d.view.EView;
import com.u2d.view.swing.StateCardPanel;
import javax.swing.*;
import org.jmatter.j1mgr.ui.RecognitionsPanel;

@Persist
public class Speaker extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   private final TextEO bio = new TextEO();
   private final ImgEO photo = new ImgEO();
   
   private final RelationalList talks = new RelationalList(Talk.class);
   public static Class talksType = Talk.class;
   public static String talksInverseFieldName = "speaker";
   
   private final RelationalList recognitions = new RelationalList(SpeakerRecognition.class);
   public static Class recognitionsType = SpeakerRecognition.class;
   
   public static String[] fieldOrder = {"name", "recognitions", "talks"};
   public static String[] tabViews = {"photo", "bio"};
   
   static
   {
      ComplexType.forClass(Speaker.class).field("recognitions").setViewHandler(new FieldViewHandler()
      {
         public EView getView(EObject value, EView defaultView)
         {
            return new StateCardPanel(new RecognitionsPanel((AbstractListEO) value), defaultView);
         }
      });
   }
   
   public Speaker()
   {
   }

   public StringEO getName() { return name; }
   public TextEO getBio() { return bio; }
   public ImgEO getPhoto() { return photo; }
   public RelationalList getTalks() { return talks; }
   public RelationalList getRecognitions() { return recognitions; }
   
   private transient PhotoIconAssistant assistant = new PhotoIconAssistant(this, photo);
   public Icon iconLg() { return assistant.iconLg(); }
   public Icon iconSm() { return assistant.iconSm(); }
   
   public Title title() { return name.title(); }
}
