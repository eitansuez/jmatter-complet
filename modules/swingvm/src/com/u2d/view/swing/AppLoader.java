package com.u2d.view.swing;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.HeadMethod;
import javax.swing.*;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
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

   private ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
   private ClassLoader _cl = initialClassLoader;
   
   private AppSession _currentSession = null;
   
   private AppLoader() {}
   
   private void verifyUrl(URL url) throws IOException
   {
      HttpClient client = new HttpClient();
      HeadMethod method = new HeadMethod(url.toString());
      try
      {
         int statusCode = client.executeMethod(method);
         if (statusCode != HttpStatus.SC_OK)
         {
            String msg = String.format( "%s (%d)", method.getStatusText(), method.getStatusCode() );
            throw new HttpException(msg);
         }
      }
      finally
      {
         method.releaseConnection();
      }
   }
   
   public void loadApplication(final URL url) throws IOException
   {
      if (url != null && !url.getProtocol().startsWith("file"))
      {
         verifyUrl(url);
      }

      if (_currentSession != null)
      {
         _currentSession.end();
         _currentSession = null;
      }
      
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
      ClassLoader cl = initialClassLoader;
      if (url != null)
      {
         cl = new URLFirstClassLoader(new URL[] { url }, initialClassLoader);
      }
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
      final Splash splash = new Splash();
      SwingViewMechanism.invokeSwingAction(new SwingAction()
      {
         public void offEDT() { initializeApp(splash); }
         public void backOnEDT() { splash.dispose(); }
      });
   }
   
   public void launchApp(Splash splash)
   {
      URL applicationContext = _cl.getResource("applicationContext.xml");
      if (applicationContext != null)
      {
         initializeApp(splash);
      }
   }
   
   private void initializeApp(Splash splash)
   {
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);
      ComplexType.reset();

      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      Application app = (Application) context.getBean("application");
      app.addAppEventListener(MESSAGE, splash);

      app.message(String.format("%s %s", ComplexType.localeLookupStatic("launching"), app.getName()));
      app.postInitialize();
      
      _currentSession = (AppSession) context.getBean("app-session");
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _currentSession.begin();
         }
      });
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
