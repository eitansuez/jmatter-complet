package com.u2d.tools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.Copy;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 18, 2006
 * Time: 12:01:02 PM
 * 
 * A work in progress.  Not Complete!!
 */
public class ProjectMaker extends Task
{
   private String _projectname;
   private File _basedir;
   private boolean _standalone;
   
   private File _targetdir;
   
   public void execute()
         throws BuildException
   {
      if (!_basedir.isDirectory())
      {
         String msg = String.format("Specified base directory %s is not a directory", _basedir);
         throw new BuildException(msg);
      }
      _targetdir = new File(_basedir, _projectname);
      if (_targetdir.exists())
      {
         String msg = String.format("Target directory %s already exists", _targetdir);
         throw new BuildException(msg);
      }
      
      if (!_targetdir.mkdir())
      {
         String msg = String.format("Creation of directory %s failed!", _targetdir);
         throw new BuildException(msg);
      }
      
      if (_standalone)
      {
         createStandaloneProject();
      }
      else
      {
         createDependentProject();
      }
   }
   
   private void createStandaloneProject()
   {
      Copy copy = (Copy) getProject().createTask("copy");
      copy.setTodir(_targetdir);
      copy.setFiltering(true);

      String resourcePath = getProject().getUserProperty("resource.dir");
      File projectTemplateDir = new File(resourcePath, "project-template");
      FileSet fileSet = new FileSet();
      fileSet.setDir(projectTemplateDir);
      copy.addFileset(fileSet);

      FilterSet filterset = copy.createFilterSet();
      filterset.addFilter("PROJECTNAME", _projectname);
      filterset.addFilter("MAINCLASSNAME", getProject().getUserProperty("main.class"));
      
      copy.execute();
   }
   private void createDependentProject()
   {
      System.out.println("tbd..");
   }

   public void setProjectname(String name) { _projectname = name; }
   public void setBasedir(File path) { _basedir = path; }
   public void setStandalone(boolean standalone) { _standalone = standalone; }

}
