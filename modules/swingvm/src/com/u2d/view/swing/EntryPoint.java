package com.u2d.view.swing;

import com.u2d.app.AppSession;
import com.u2d.app.Application;
import com.u2d.element.Member;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 8, 2007
 * Time: 12:41:34 PM
 */
public class EntryPoint
{
   public static void main(String[] args)
   {
      SwingViewMechanism.setupAntiAliasing();
      Splash splash = new Splash();

      try
      {
         Logger.getLogger("org.springframework").setLevel(Level.WARNING);
         ApplicationContext context = 
               new ClassPathXmlApplicationContext("applicationContext.xml");
      
         Application app = (Application) context.getBean("application");
         app.addAppEventListener("MESSAGE", splash);
      
         app.postInitialize();
      
         AppSession session = (AppSession) context.getBean("app-session");
         session.launch();
      
         Member.mergeInDbMetadata();
      }
      finally
      {
         splash.dispose();
      }
   }

}
