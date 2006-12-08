/*
 * Created on Jan 30, 2004
 */
package com.u2d.app;

import com.u2d.model.ComplexType;
import com.u2d.type.composite.Folder;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.type.LogEventType;
import com.u2d.ui.Splash;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.element.EOCommand;
import com.u2d.element.Member;
import com.u2d.xml.CodesList;
import org.hibernate.*;
import org.jibx.runtime.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Eitan Suez
 */
public class Application
{
   protected String _name;
   protected int _pagesize;
   protected PersistenceMechanism _pmech;
   protected Splash _splash; 
   private Folder _classBar;

   public Application() {}
   
   public String getName() { return _name; }
   public void setName(String name) { _name = name; }
   
   public int getPagesize() { return _pagesize; }
   public void setPagesize(int size) { _pagesize = size; }
   
   public PersistenceMechanism getPersistenceMechanism() { return _pmech; }
   public void setPersistenceMechanism(PersistenceMechanism pmech)
   {
      _pmech = pmech;
   }
   
   public void setSplash(Splash splash) { _splash = splash; }

   public void initialize()
   {
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);

      // set repaintmanager for debugging EDT issues:
//      RepaintManager.setCurrentManager(new spin.over.CheckingRepaintManager());

      String launchingMsg = String.format("Launching %s", _name);
      message(launchingMsg);
   }
   
   private static final String TEMPLATE_CLASSBAR = "Template Class Bar";
   public void postInitialize()
   {
      makeClassBar();

      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
      Session session = hbm.getSession();

      String hql = "select count(*) from com.u2d.app.User u where u.username='admin'";
      int count = ((Long) session.createQuery(hql).iterate().next()).intValue();
      if (count == 0)
      {
         message("Creating Admin User and base roles..");
         Role adminRole = new Role("Administrator");
         Role defaultRole = new Role("Default");
         User adminUser = new User("admin", "admin", adminRole);
         Set items = new HashSet();
         items.add(adminUser);
         items.add(adminRole);
         items.add(defaultRole);
         hbm.saveMany(items);
         
         // skip this for now..
//            defaultRole.initializePermissions(hbm);
         CodesList.populateCodes(_pmech, hbm.getClasses());
      }

      ComplexType.associateQueries(_pmech);
   }
   
   private void makeClassBar()
   {
      _classBar = Folder.fetchFolderByName(_pmech, TEMPLATE_CLASSBAR);
      if (_classBar == null)
      {
         _classBar = loadXMLClassList();
         ((HBMPersistenceMechanism) _pmech).saveMany(_classBar.getSelfAndNestedFolders());
      }
   }
   public Folder getClassBar() { return _classBar; }

   private Folder loadXMLClassList()
   {
      try
      {
         IBindingFactory bfact = BindingDirectory.getFactory(Folder.class);
         IUnmarshallingContext context = bfact.createUnmarshallingContext();

         InputStream stream = getClass().getResourceAsStream("/com/u2d/class-list.xml");
         Folder templateFolder = (Folder) context.unmarshalDocument(stream, null);
         templateFolder.getName().setValue(TEMPLATE_CLASSBAR);
         return templateFolder;
      }
      catch (JiBXException ex)
      {
         System.err.println("JiBXException: "+ex.getMessage());
         ex.printStackTrace();
         System.exit(1);
      }
      return null;
   }

   private void message(String msg)
   {
      Tracing.tracer().info(msg);
      if (_splash != null)
         _splash.message(msg);
   }

   public void log(String typeString, EOCommand cmd, String msg)
   {
      //LoggedEvent event = new LoggedEvent(type, this, cmd, msg);
      // still thinking this through..
      LogEventType type = new LogEventType(typeString);
//      LoggedEvent event = new LoggedEvent(type, null, cmd, msg);
      /* temporarily not saving the command.  commands and fields
         are in a process of transition from being custom user types
         to being Entities in their own right.
         To go back to the line above, i would have to make sure
         i save the command otherwise i'll get a transient object
         exception from hibernate.
       */
      LoggedEvent event = new LoggedEvent(type, null, null, msg);
      event.setTransientState();
      event.save();
   }

   public static void main(String[] args)
   {
      SwingViewMechanism.setupAntiAliasing();
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);
      ApplicationContext context = 
            new ClassPathXmlApplicationContext("applicationContext.xml");
      
      Application app = (Application) context.getBean("application");
      app.postInitialize();
      
      AppSession session = (AppSession) context.getBean("app-session");
      session.launch();
      
      Member.mergeInDbMetadata();
      
      Splash splash = (Splash) context.getBean("splash");
      splash.dispose();
   }

}
