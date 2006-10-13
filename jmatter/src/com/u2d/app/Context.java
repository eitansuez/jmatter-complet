package com.u2d.app;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import com.u2d.view.swing.SwingViewMechanism;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 19, 2006
 * Time: 3:04:22 PM
 * 
 * Wrapper for Spring's applicationContext..
 */
public class Context implements ApplicationContextAware
{
   private static Context _context = new Context();
   public static Context getInstance() { return _context; }

   NullViewMechanism _nullvmech = new NullViewMechanism();
   public Context() { }

   private ApplicationContext _applicationContext;

   public void setApplicationContext(ApplicationContext applicationContext)
         throws BeansException
   {
      _applicationContext = applicationContext;
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
   
   public SwingViewMechanism swingvmech()
   {
      return (SwingViewMechanism) getViewMechanism();
   }
   public HBMPersistenceMechanism hbmpersitor()
   {
      return (HBMPersistenceMechanism) getPersistenceMechanism();
   }
}
