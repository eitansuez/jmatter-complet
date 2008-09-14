package org.jmatter.appbuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Sep 14, 2008
 * Time: 4:31:17 PM
 */
public class Application extends com.u2d.app.Application
{
   public void postInitialize()
   {
      super.postInitialize();
      contributeToIndex(ProjectAB.class, EntityAB.class);
   }
}
