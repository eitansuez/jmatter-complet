package com.u2d.appbrowser;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 13, 2008
 * Time: 4:46:25 PM
 */
public class Application extends com.u2d.app.Application
{
   public void postInitialize()
   {
      super.postInitialize();
      contributeToIndex(AppBookmark.class);
   }
}
