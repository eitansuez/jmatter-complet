/*
 * Created on Jan 30, 2004
 */
package com.u2d.app;

import com.u2d.model.ComplexType;
import com.u2d.pubsub.*;
import com.u2d.type.atom.*;
import com.u2d.type.composite.Folder;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.type.LogEventType;
import com.u2d.ui.Splash;
import com.u2d.restrict.Restriction;
import com.u2d.persist.HBMGenerator;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.element.EOCommand;
import com.u2d.xml.CodesList;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.jibx.runtime.*;
import javax.swing.*;
import java.util.*;
import java.io.InputStream;

/**
 * @author Eitan Suez
 */
public class Application implements AuthManager, AppEventNotifier
{
   protected String _appName;
   protected int _pagesize = 20;
   protected transient ViewMechanism _vmech;
   protected PersistenceMechanism _pmech;
   protected Folder _classesFolder;
   protected String _lfName;
   protected ArrayList _persistClasses;

   private ApplicationContext _fsm;
   private User _user;

   private Splash _splash;

   public Application() { this(false); }

   public Application(boolean headless)
   {
      AppFactory.getInstance().setApp(this);
      startSplashScreen(headless);
      loadXMLConfigInfo();
      _fsm = new ApplicationContext(this);
      _pmech.init((Class[]) _persistClasses.toArray(new Class[0]));
   }
   
   public Application(HBMGenerator generator)
   {
      loadXMLConfigInfo();
      generator.processClassList(getPersistClasses());
   }
   
   private void startSplashScreen(boolean headless)
   {
      if (headless) return;
      
      // set repaintmanager for debugging EDT issues:
//      RepaintManager.setCurrentManager(
//            new spin.over.CheckingRepaintManager() );

      // for antialiasing to work, it must be setup before
      // anything is drawn:
      SwingViewMechanism.setupAntiAliasing();
      _splash = new Splash(_appName);
   }

   private void loadXMLConfigInfo()
   {
      try
      {
         IBindingFactory bfact = BindingDirectory.getFactory(Application.class);
         IUnmarshallingContext context = bfact.createUnmarshallingContext();

         InputStream stream = getClass().getResourceAsStream("/com/u2d/app-config.xml");
         context.setDocument(stream, null);

         ((IUnmarshallable) this).unmarshal(context);
      }
      catch (JiBXException ex)
      {
         System.err.println("JiBXException: "+ex.getMessage());
         ex.printStackTrace();
         System.exit(1);
      }
   }
   
   public void initObjects()
   {
      // create admin user
      if (hbmpersistence())
      {
         HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
         Session session = hbm.getSession();

         String hql = "select count(*) from com.u2d.app.User as user where user.username='admin'";
         int count = ((Integer) session.createQuery(hql).iterate().next()).intValue();
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
         
         CodesList.populateCodes(_persistClasses);
      }
      
      _classesFolder = Folder.fetchFolderByName("Class List");
      if (_classesFolder == null)
      {
         _classesFolder = loadXMLClassList();
         _classesFolder.save();
      }
   }
   
   private Folder loadXMLClassList()
   {
      try
      {
         IBindingFactory bfact = BindingDirectory.getFactory(Application.class);
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


   public void launch()
   {
      initObjects();
      ComplexType.associateQueries();
      
      _vmech.launch();
      _fsm.onBegin();
      
      _splash.dispose();
   }
   
   /* setter methods expected by jibx when unmarshalling */
   private void setPersistenceMechanism(String clsName) throws Exception { _pmech = (PersistenceMechanism) instantiate(Class.forName(clsName)); }
   private void setViewMechanism(String clsName) throws Exception { _vmech = (ViewMechanism) instantiate(Class.forName(clsName)); }
   public String placebo() { return ""; }
   
   public String getName() { return _appName; }
   public int getPageSize() { return _pagesize; }
   public String getLFName() { return _lfName; }
   public ViewMechanism getViewMechanism() { return _vmech; }
   public PersistenceMechanism getPersistenceMechanism() { return _pmech; }
   public Folder getClassesFolder() { return _classesFolder; }
   public List getPersistClasses() { return _persistClasses; }
   
   private void message(String msg)
   {
      Tracing.tracer().info(msg);
      if (_splash != null) _splash.message(msg);
   }

   public User getUser() { return _user; }
   
   /* package private */ void setUser(User user)
   {
      if (user == null && hbmpersistence())
      {
         liftRestrictions(_user.getRole().getCmdRestrictions().getItems());
         liftRestrictions(_user.getRole().getFldRestrictions().getItems());
      }

      _user = user;

      if (_user!=null && hbmpersistence())
      {
         applyRestrictions(_user.getRole().getCmdRestrictions().getItems());
         applyRestrictions(_user.getRole().getFldRestrictions().getItems());
      }
   }

   private boolean hbmpersistence()
   {
      return _pmech instanceof HBMPersistenceMechanism;
   }

   private void applyRestrictions(Collection restrictions)
   {
      doRestrictions(restrictions, true);
   }
   private void liftRestrictions(Collection restrictions)
   {
      doRestrictions(restrictions, false);
   }
   private void doRestrictions(Collection restrictions, boolean apply)
   {
      Iterator itr = restrictions.iterator();
      Restriction restriction = null;
      while (itr.hasNext())
      {
         restriction = (Restriction) itr.next();
         if (apply)
            restriction.element().applyRestriction(restriction);
         else
            restriction.element().liftRestriction(restriction);
      }
   }


   //
   // == authentication-related methods ===
   // see related Application.sm  (smc rocks!!)
   //

   private Map _badAttempts;
   private int THRESHOLD = 3;

   public void showLoginDialog() { _vmech.showLogin(); }

   public void dismissLoginDialog()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _vmech.dismissLogin();
         }
      });
   }

   public void loginInvalid() { _vmech.loginInvalid(); }
   public void displayLockedDialog() { _vmech.userLocked(); }

   public void setupUser(final String username)
   {
      new Thread()
      {
         public void run()
         {
            User user = null;
            if (hbmpersistence())
            {
               HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
               Session session = hbm.getSession();
               try
               {
                  user = (User) session.createCriteria(User.class).add(
                          Restrictions.eq("username", new StringEO(username))
                        ).uniqueResult();
               }
               catch (HibernateException ex)
               {
                  System.err.println("HibernateException: "+ex.getMessage());
                  ex.printStackTrace();
                  // TODO: throw an exception that translates into a truthful message to the end user
                  return;
               }
            }
            else
            {
               user = new User(username);
            }
            user.onLoad();
            setUser(user);
            log(LoggedEvent.LOGIN, null, "Logged In");
            fireAppEventNotification("LOGIN");
         }
      }.start();
   }

   public void clearUser()
   {
      log(LoggedEvent.LOGOUT,  null, "Logged Out");
      fireAppEventNotification("LOGOUT");
      setUser(null);
   }

   public boolean authenticate(final String username, final String password)
   {
      class AuthThread extends Thread
      {
         boolean authResult = false;
         public void run()
         {
            authResult = _pmech.authenticate(username, password);
         }
         public boolean authResult() { return authResult; }
      }
      AuthThread t = new AuthThread();
      t.start();
      try
      {
         t.join(10000);
         return t.authResult();
      }
      catch (InterruptedException ex)
      {
         ex.printStackTrace();
         return false;
      }
   }

   public void clearBadAttempts(String username)
   {
      if (_badAttempts == null)
         _badAttempts = new HashMap();
      _badAttempts.put(username, new Integer(0));
   }
   public boolean tooManyBadAttempts(String username)
   {
      if (_badAttempts == null)
         _badAttempts = new HashMap();
      Integer count = (Integer) _badAttempts.get(username);
      if (count == null)
      {
         _badAttempts.put(username, new Integer(1));
         return false;
      }
      int num = count.intValue() + 1;
      _badAttempts.put(username, new Integer(num));
      return num >= THRESHOLD;
   }
   private boolean _locked = false;
   public boolean isLocked(String username)
   {
      if (hbmpersistence())
      {
         HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
         Session session = hbm.getSession();
         try
         {
            Query query = session.createQuery("select user.locked from com.u2d.app.User as user where user.username = :username");
            query.setString("username", username);
            BooleanEO islocked = (BooleanEO) query.uniqueResult();
            if (islocked == null) return false;  // no such user
            return islocked.booleanValue();
         }
         catch (HibernateException ex)
         {
            System.err.println("HibernateException: "+ex.getMessage());
            ex.printStackTrace();
            return false;  // TODO: throw an exception that translates into a truthful message to the end user
         }
      }
      else
      {
         // assume a mock mechanism..
         return _locked;
      }
   }
   public void lock(final String username)
   {
      new Thread()
      {
         public void run()
         {
            if (hbmpersistence())
            {
               HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _pmech;
               Session session = hbm.getSession();
               Query query = session.createQuery("from com.u2d.app.User as user where user.username = :username");
               query.setString("username", username);
               User user = (User) query.uniqueResult();
               if (user == null) return; // no such user
               user.getLocked().setValue(true);
               user.save();
            }
            else
            {
               _locked = true;
            }
         }
      }.start();
   }

   // == "authmgr" interface

   public void onLogin(String username, String password)
   {
      _fsm.onLogin(username, password);
   }
   public void onLogout()
   {
      _fsm.onLogout();
   }


   // == app event notifier ..

   private transient AppEventSupport support = new AppEventSupport(this);
   public void addAppEventListener(String evtType, AppEventListener l)
   {
      support.addAppEventListener(evtType, l);
   }
   public void removeAppEventListener(String evtType, AppEventListener l)
   {
      support.removeAppEventListener(evtType, l);
   }
   public void fireAppEventNotification(String evtType)
   {
      support.fireAppEventNotification(evtType);
   }
   public void fireAppEventNotification(String evtType, Object target)
   {
      support.fireAppEventNotification(evtType, target);
   }

   
   // === misc..
   private Object instantiate(Class clazz)
   {
      try
      {
         java.lang.reflect.Method m = clazz.getMethod("getInstance", null);
         return m.invoke(null, null);
      }
      catch (Exception ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
         throw new RuntimeException("Failed to Construct "+clazz.getName());
      }
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
      new Application().launch();
   }

}
