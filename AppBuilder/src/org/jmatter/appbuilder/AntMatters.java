package org.jmatter.appbuilder;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 6, 2008
 * Time: 1:57:37 PM
 */
public class AntMatters
{
   public static final Project jmatterProject = AntMatters.forProject("../jmatter/build.xml");  // relative to appbuilder dir
   private AntMatters() {}

   public static Project forProject(String path)
   {
      return forProject(new File(path));
   }
   public static Project forProject(File buildFile)
   {
      Project p = new Project();
      p.setUserProperty("ant.file", buildFile.getAbsolutePath());

      p.init();
      ProjectHelper helper = ProjectHelper.getProjectHelper();
      p.addReference("ant.projectHelper", helper);
      helper.parse(p, buildFile);

      return p;
   }

}
