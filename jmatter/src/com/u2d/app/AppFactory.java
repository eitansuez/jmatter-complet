package com.u2d.app;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 13, 2005
 * Time: 12:27:19 PM
 */
public class AppFactory
{
   static AppFactory _instance;
   
   private Application _app;
   
   public synchronized static AppFactory getInstance()
   {
      if (_instance == null)
      {
         _instance = new AppFactory();
      }
      return _instance;
   }
   
   public Application getApp() { return _app; }
   public void setApp(Application app) { _app = app; }
}
