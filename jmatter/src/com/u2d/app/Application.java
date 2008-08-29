/*
 * Created on Jan 30, 2004
 */
package com.u2d.app;

import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.type.composite.Folder;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.type.LogEventType;
import com.u2d.element.EOCommand;
import com.u2d.json.CodesList;
import com.u2d.json.JSON;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class Application implements AppEventNotifier
{
   protected String _name, _version, _description, _helpContentsUrl;
   protected int _pagesize;
   protected PersistenceMechanism _pmech;
   private Folder _classBar;
   
   private transient Map<String, ComplexEObject> index = new HashMap<String, ComplexEObject>();

   
   public Application() {}
   
   public String getName() { return _name; }
   public void setName(String name) { _name = name; }
   
   public String getVersion() { return _version; }
   public void setVersion(String version) { _version = version; }
   
   public String title() { return _name + " v" + _version; }

   public String getDescription() { return _description; }
   public void setDescription(String description) { _description = description; }

   public String getHelpContentsUrl() { return _helpContentsUrl; }
   public void setHelpContentsUrl(String helpContentsUrl) { _helpContentsUrl = helpContentsUrl; }

   public int getPagesize() { return _pagesize; }
   public void setPagesize(int size) { _pagesize = size; }
   
   public PersistenceMechanism getPersistenceMechanism() { return _pmech; }
   public void setPersistenceMechanism(PersistenceMechanism pmech)
   {
      _pmech = pmech;
   }
   
   public void initialize()
   {
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);

      // set repaintmanager for debugging EDT issues:
//      RepaintManager.setCurrentManager(new spin.over.CheckingRepaintManager());
   }
   
   public void postInitialize()
   {
      seedDatabase();
      addTypesToIndex();
   }

   /**
    * override this to initialize permissions (one-time)
    */
   public void initializePermissions() {}

   private void addTypesToIndex()
   {
      contributeToIndex(topLevelEntities());
   }

   protected AbstractListEO topLevelEntities()
   {
      return ComplexType.persistedTypes();
   }

   public boolean isAppBrowser()
   {
      return getName().equals("JMatter App Browser");
   }

   /*
   optional:  override in a subclass and specify a service object
   and its commands will appear in the application frame in a commands toolbar
    */
   public EObject serviceObject()
   {
      return null;
   }
   
   public void contributeToIndex(ComplexEObject eo)
   {
      index.put(eo.title().toString().toLowerCase(), eo);
   }
   public void contributeToIndex(AbstractListEO items)
   {
      for (Iterator itr = items.iterator(); itr.hasNext(); )
      {
         contributeToIndex((ComplexEObject) itr.next());
      }
   }
   public void contributeToIndex(Class... types)
   {
      for (Class type : types)
      {
         contributeToIndex(type);
      }
   }
   private void contributeToIndex(Class type)
   {
      AbstractListEO contacts = getPersistenceMechanism().list(type);
      contributeToIndex(contacts);

      ComplexType.forClass(type).addAppEventListener(AppEventType.CREATE,
              new AppEventListener()
              {
                 public void onEvent(AppEvent evt)
                 {
                    contributeToIndex((ComplexEObject) evt.getEventInfo());
                 }
              });
   }
   
   public Map<String, ComplexEObject> getIndex() { return index; }
   
   
   
   private static final String TEMPLATE_CLASSBAR = "Template Class Bar";
   public void seedDatabase()
   {
      makeClassBar();

      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
      Session session = hbm.getSession();

      String hql = "select count(*) from com.u2d.app.User u where u.username='admin'";
      int count = ((Long) session.createQuery(hql).iterate().next()).intValue();
      if (count == 0)
      {
         message("Creating base users and roles..");
         Role adminRole = new Role("Administrator");
         Role defaultRole = new Role("Default");
         Set<EObject> items = new HashSet<EObject>();
         items.add(adminRole);
         items.add(defaultRole);
         items.add(new User("admin", "admin", adminRole));
         items.add(new User("johndoe", "johndoe", defaultRole));
         hbm.saveMany(items);
         
         defaultRole.initializePermissions(hbm);
         CodesList.populateCodes(_pmech, hbm.getClasses());
      }

      ComplexType.associateQueries(_pmech);
   }
   
   private void makeClassBar()
   {
      _classBar = Folder.fetchFolderByName(_pmech, TEMPLATE_CLASSBAR);
      if (_classBar == null)
      {
         _classBar = loadJSONClassList();
         ((HBMPersistenceMechanism) _pmech).saveMany(_classBar.getSelfAndNestedFolders());
      }
   }
   public Folder getClassBar() { return _classBar; }

   private Folder loadJSONClassList()
   {
      try
      {
         Folder templateFolder = (Folder) JSON.readJsonFromResource("class-list.json");
         templateFolder.getName().setValue(TEMPLATE_CLASSBAR);
         return templateFolder;
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Failed to unmarshal class-list.json", ex);
      }
   }

   public void message(String msg)
   {
      Tracing.tracer().info(msg);
      fireAppEventNotification(MESSAGE, msg);
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

   // == app event notifier ..

   private transient AppEventSupport support = new AppEventSupport(this);
   public void addAppEventListener(AppEventType evtType, AppEventListener l)
   {
      support.addAppEventListener(evtType, l);
   }
   public void removeAppEventListener(AppEventType evtType, AppEventListener l)
   {
      support.removeAppEventListener(evtType, l);
   }
   public void fireAppEventNotification(AppEventType evtType)
   {
      support.fireAppEventNotification(evtType);
   }
   public void fireAppEventNotification(AppEventType evtType, Object target)
   {
      support.fireAppEventNotification(evtType, target);
   }


   public static void main(String[] args)
   {
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);
      ApplicationContext context = 
            new ClassPathXmlApplicationContext("applicationContext.xml");
      ((Application) context.getBean("application")).seedDatabase();
   }

}
