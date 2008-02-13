package com.u2d.appbrowser;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.URI;
import com.u2d.type.atom.TextEO;
import com.u2d.persist.Persist;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.view.swing.AppLoader;

@Persist
public class AppBookmark
      extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "url", "description"};
   
   private final URI url = new URI();
   /*
   TODO:
     url is only entered property
     others set and saved after app loads.
     that is, when app loads, get app.getName() app.getDescription(), app.getIcon() (?)
       and then can set them on the bookmark.  though let end user edit name and description
       on the bookmark if they like.
    */
   
   private final StringEO name = new StringEO();
   private final TextEO description = new TextEO();
   
   public AppBookmark()
   {
   }
   
   public URI getUrl() { return url; }
   public StringEO getName() { return name; } 
   public TextEO getDescription() { return description; }
   
   @Cmd(mnemonic='l')
   public void Launch(CommandInfo cmdInfo)
   {
      AppLoader.getInstance().loadApplication(url.urlValue());
   }

   public String defaultCommandName() { return "Launch"; }

   public Title title() { return name.title(); }
   
}
