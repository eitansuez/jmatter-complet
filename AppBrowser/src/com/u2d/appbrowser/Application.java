package com.u2d.appbrowser;

import com.u2d.model.AbstractListEO;

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
      AbstractListEO bookmarks = getPersistenceMechanism().list(AppBookmark.class);
      contributeToIndex(bookmarks);
   }
}
