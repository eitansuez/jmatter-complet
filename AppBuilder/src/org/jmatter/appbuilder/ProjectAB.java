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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

import java.io.File;

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
      Project p = AntMatters.jmatterProject;
      try
      {
         p.fireBuildStarted();

         p.setProperty("new.project.name", name.stringValue());
         p.setProperty("new.project.basedir", parentDirectory.stringValue());
         p.executeTarget("new-project");

         p.fireBuildFinished(null);
         return "Done";
      }
      catch (BuildException ex)
      {
         p.fireBuildFinished(ex);
         return "Error: "+ex.getMessage();
      }
   }

   private Project antProject;
   private synchronized Project antProject()
   {
      if (antProject == null)
      {
         File projectDir = new File(parentDirectory.fileValue(), name.stringValue());
         File buildFile = new File(projectDir, "build.xml");
         antProject = AntMatters.forProject(buildFile);
      }
      return antProject;
   }
   @Cmd
   public String SchemaExport(CommandInfo cmdInfo)
   {
      return runTarget("schema-export");
   }
   @Cmd
   public String RunApp(CommandInfo cmdInfo)
   {
      return runTarget("run");
   }

   private String runTarget(String target)
   {
      Project p = antProject();
      try
      {
         p.fireBuildStarted();

         p.executeTarget(target);

         p.fireBuildFinished(null);
         return "Done";
      }
      catch (BuildException ex)
      {
         p.fireBuildFinished(ex);
         return "Error: "+ex.getMessage();
      }
   }

   public static String naturalName()
   {
      return "Project";
   }

}
