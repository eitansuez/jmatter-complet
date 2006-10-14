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
import com.u2d.xml.CodesList;
import org.hibernate.*;
import org.jibx.runtime.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.InputStream;

/**
 * @author Eitan Suez
 */
public class Application
{
   protected String _name;
   protected int _pagesize;
   protected PersistenceMechanism _pmech;
   protected Splash _splash; 

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
      // set repaintmanager for debugging EDT issues:
//      RepaintManager.setCurrentManager(new spin.over.CheckingRepaintManager());

      String launchingMsg = String.format("Launching %s", _name);
      message(launchingMsg);
      
      // create admin user
      if (_pmech instanceof HBMPersistenceMechanism)
      {
         HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
         Session session = hbm.getSession();

         String hql = "select count(*) from com.u2d.app.User u where u.username='admin'";
         int count = ((Long) session.createQuery(hql).iterate().next()).intValue();
         if (count == 0)
         {
            Transaction tx = session.beginTransaction();
            message("Creating Admin User..");
            Role adminRole = new Role("Administrator");
            User adminUser = new User("admin", "admin", adminRole);
            session.save(adminUser);
            session.save(adminRole);
            tx.commit();
         }
         
         CodesList.populateCodes(_pmech, hbm.getClasses());
      }

      Folder classesFolder = Folder.fetchFolderByName(_pmech, "Class List");
      if (classesFolder == null)
      {
         classesFolder = loadXMLClassList();
         ((HBMPersistenceMechanism) _pmech).saveMany(classesFolder.getSelfAndNestedFolders());
      }

      ComplexType.associateQueries(_pmech);
   }

   private Folder loadXMLClassList()
   {
      try
      {
         IBindingFactory bfact = BindingDirectory.getFactory(Folder.class);
         IUnmarshallingContext context = bfact.createUnmarshallingContext();

         InputStream stream = getClass().getResourceAsStream("/com/u2d/class-list.xml");
         return (Folder) context.unmarshalDocument(stream, null);
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
      LoggedEvent event = new LoggedEvent(type, null, cmd, msg);
      event.setTransientState();
      event.save();
   }

   public static void main(String[] args)
   {
      SwingViewMechanism.setupAntiAliasing();
      ApplicationContext context = 
            new ClassPathXmlApplicationContext("applicationContext.xml");
      
      AppSession session = (AppSession) context.getBean("app-session");
      session.launch();

      Splash splash = (Splash) context.getBean("splash");
      splash.dispose();
   }

}
