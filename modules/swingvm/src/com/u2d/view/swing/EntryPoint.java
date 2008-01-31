package com.u2d.view.swing;

import java.net.URL;

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
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         URL applicationContext = loader.getResource("applicationContext.xml");
      
         if (applicationContext == null)
         {
            SwingViewMechanism.getInstance().launch();
         }
         else
         {
            AppLoader.getInstance().launchApp(splash);
         }
      }
      finally
      {
         splash.dispose();
      }

   }

}
