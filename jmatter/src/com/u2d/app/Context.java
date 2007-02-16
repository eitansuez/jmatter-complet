package com.u2d.app;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 19, 2006
 * Time: 3:04:22 PM
 * 
 * Wrapper for Spring's applicationContext..
 */
public class Context implements ApplicationContextAware, AppEventNotifier
{
   private static Context _context = new Context();
   public static Context getInstance() { return _context; }

   NullViewMechanism _nullvmech = new NullViewMechanism();
   public Context() { }

   private ApplicationContext _applicationContext;
   private AppEventSupport _support = new AppEventSupport(this);

   public void setApplicationContext(ApplicationContext applicationContext)
         throws BeansException
   {
      _applicationContext = applicationContext;
      fireAppEventNotification(APP_READY);
   }


   public AppSession getAppSession()
   {
      return (AppSession) _applicationContext.getBean("app-session");
   }
   public Application getApplication()
   {
      return (Application) _applicationContext.getBean("application");
   }
   public PersistenceMechanism getPersistenceMechanism()
   {
      return (PersistenceMechanism) _applicationContext.getBean("persistor");
   }
   public ViewMechanism getViewMechanism()
   {
      if (_applicationContext == null) return _nullvmech;
      return (ViewMechanism) _applicationContext.getBean("view-mechanism");
   }

   // ---
   
   public HBMPersistenceMechanism hbmpersitor()
   {
      return (HBMPersistenceMechanism) getPersistenceMechanism();
   }

   // ---

   public void addAppEventListener(AppEventType evtType, AppEventListener l)
   {
      if (_applicationContext == null)
      {
         _support.addAppEventListener(evtType, l);
      }
      else
      {
         // immediately message.  event already occurred..
         l.onEvent(new AppEvent(this, evtType));
      }
   }

   public void removeAppEventListener(AppEventType evtType, AppEventListener l)
   {
      _support.removeAppEventListener(evtType, l);
   }

   public void fireAppEventNotification(AppEventType evtType)
   {
      _support.fireAppEventNotification(evtType);
   }

}
