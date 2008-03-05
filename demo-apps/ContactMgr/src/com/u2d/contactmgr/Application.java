package com.u2d.contactmgr;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.pubsub.AppEventType;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 14, 2008
 * Time: 9:49:45 AM
 */
public class Application extends com.u2d.app.Application
{
   public void postInitialize()
   {
      super.postInitialize();
      AbstractListEO bookmarks = getPersistenceMechanism().list(PersonContact.class);
      contributeToIndex(bookmarks);
      
      ComplexType.forClass(PersonContact.class).addAppEventListener(AppEventType.CREATE, 
              new AppEventListener()
              {
                 public void onEvent(AppEvent evt)
                 {
                    contributeToIndex((ComplexEObject) evt.getEventInfo());
                 }
              });
   }
}
