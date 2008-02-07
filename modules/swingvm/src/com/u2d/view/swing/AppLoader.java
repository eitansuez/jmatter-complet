package com.u2d.view.swing;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import javax.swing.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.u2d.app.Application;
import com.u2d.app.AppSession;
import static com.u2d.pubsub.AppEventType.MESSAGE;
import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 28, 2008
 * Time: 6:18:41 PM
 */
public class AppLoader implements ThreadMaker
{
   public static AppLoader _appLoader = new AppLoader();
   public static AppLoader getInstance() { return _appLoader; }

   private ClassLoader _cl = Thread.currentThread().getContextClassLoader();
   
   private AppLoader() {}
   
   public void loadApplication(final URL url)
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         loadApp(url);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               loadApp(url);
            }
         });
      }
   }
   
   private void loadApp(URL url)
   {
      ClassLoader cl = URLClassLoader.newInstance(new URL[] { url }, ClassLoader.getSystemClassLoader());
      updateContextClassLoader(cl);
      loadApp();
   }
   
   private void updateContextClassLoader(ClassLoader cl)
   {
     Thread.currentThread().setContextClassLoader(cl);  // update edt class loader..
     setClassLoader(cl);
   }
   
   private void setClassLoader(ClassLoader cl) { _cl = cl; }

   private void loadApp()
   {
      newThread(new Runnable()
      {
         public void run()
         {
            Splash splash = new Splash();

            try
            {
               AppSession session = initializeApp(splash);
               session.begin();
            }
            finally
            {
               splash.dispose();
            }
         }
      }).start();
   }
   
   public void launchApp(Splash splash)
   {
      AppSession session = initializeApp(splash);
      session.launch();
   }
   
   private AppSession initializeApp(Splash splash)
   {
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);
      ComplexType.loadMetadataProperties();
      ComplexType.loadLocaleBundle();
      
      ClassPathXmlApplicationContext context = 
            new ClassPathXmlApplicationContext("applicationContext.xml");
      
      Application app = (Application) context.getBean("application");
      app.addAppEventListener(MESSAGE, splash);

      app.message(String.format("Launching %s", app.getName()));
      app.seedDatabase();
      
      return (AppSession) context.getBean("app-session");
   }
   
   public Thread newThread()
   {
      Thread t = new Thread();
      t.setContextClassLoader(_cl);
      return t;
   }
   public Thread newThread(Runnable runnable)
   {
      Thread t = new Thread(runnable);
      t.setContextClassLoader(_cl);
      return t;
   }
   
}
