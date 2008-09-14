package org.jmatter.appbuilder;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.FileEO;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Fld;
import com.u2d.element.CommandInfo;

import javax.persistence.Entity;

@Entity
public class ProjectAB extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "caption", "description", "splashImage", "defaultPackageName"};

   public ProjectAB() { }

   private final FileEO parentDirectory = new FileEO();
   public FileEO getParentDirectory() { return parentDirectory; }
   
   private final StringEO name = new StringEO();
   @Fld(description="Name is also used as directory name where project lives (or will live) on disk")
   public StringEO getName() { return name; }

   private final StringEO caption = new StringEO();
   public StringEO getCaption() { return caption; }

   private final TextEO description = new TextEO();
   public TextEO getDescription() { return description; }

   private final ImgEO splashImage = new ImgEO();
   public ImgEO getSplashImage() { return splashImage; }

   private final StringEO defaultPackageName = new StringEO();
   public StringEO getDefaultPackageName() { return defaultPackageName; }

   public Title title() { return name.title(); }

   @Cmd(mnemonic='a')
   public EntityAB AddEntity(CommandInfo cmdInfo)
   {
      EntityAB entity = (EntityAB) createInstance(EntityAB.class);
      entity.association("project").set(this);
      return entity;
   }
   
   @Cmd(description="Generate project's skeleton directory structure and files")
   public String CreateProjectSkeleton(CommandInfo cmdInfo)
   {
      return "TBD";
   }
   

   public static String naturalName()
   {
      return "Project";
   }

}
