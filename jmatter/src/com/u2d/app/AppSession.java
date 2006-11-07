package com.u2d.app;

import com.u2d.pubsub.AppEventNotifier;
import com.u2d.pubsub.AppEventSupport;
import com.u2d.pubsub.AppEventListener;
import com.u2d.restrict.Restriction;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.type.composite.Folder;
import com.u2d.element.EOCommand;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 14, 2006
 * Time: 12:37:59 PM
 */
public class AppSession implements AuthManager, AppEventNotifier
{
   protected Application _app;
   protected ViewMechanism _vmech;
   
   protected AppSessionContext _fsm;
   protected User _user;
   protected Folder _classesFolder;

   public AppSession() {}

   public void initialize()
   {
      _fsm = new AppSessionContext(this);
      _classesFolder = Folder.fetchFolderByName(pmech(), "Class List");
   }
   
   public void launch()
   {
      _vmech.launch();
      _fsm.onBegin();
   }
   
   public Application getApp() { return _app; }
   public void setApp(Application app) { _app = app; }
   
   public ViewMechanism getViewMechanism() { return _vmech; }
   public void setViewMechanism(ViewMechanism vmech) { _vmech = vmech; }

   // convenience.. revisit this
   private PersistenceMechanism pmech() { return _app.getPersistenceMechanism(); }
   private boolean hbmpersistence()
   {
      return (pmech() instanceof HBMPersistenceMechanism);
   }
   private void log(String typeString, EOCommand cmd, String msg) { _app.log(typeString, cmd, msg); }


   public User getUser() { return _user; }

   /* package private */ void setUser(User user)
   {
      if (user == null && hbmpersistence())
      {
         liftRestrictions(_user.getRole().getRestrictions().getItems());
      }

      _user = user;

      if (_user!=null && hbmpersistence())
      {
         applyRestrictions(_user.getRole().getRestrictions().getItems());
      }
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
            restriction.element().liftRestriction();
      }
   }

   public Folder getClassesFolder() { return _classesFolder; }

   //
   // == authentication-related methods ===
   // see related AppSession.sm
   //

   private Map<String, Integer> _badAttempts;
   private int THRESHOLD = 3;

   public void showLoginDialog() { _vmech.showLogin(); }
   public void dismissLoginDialog() { _vmech.dismissLogin(); }
   public void loginInvalid() { _vmech.loginInvalid(); }
   public void displayLockedDialog() { _vmech.userLocked(); }

   public void setupUser(final String username)
   {
      User user = null;
      if (hbmpersistence())
      {
         HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech();
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

   public void clearUser()
   {
      log(LoggedEvent.LOGOUT,  null, "Logged Out");
      fireAppEventNotification("LOGOUT");
      setUser(null);
   }

   public boolean authenticate(final String username, final String password)
   {
      return pmech().authenticate(username, password);
   }

   public void clearBadAttempts(String username)
   {
      if (_badAttempts == null)
         _badAttempts = new HashMap<String, Integer>();
      _badAttempts.put(username, new Integer(0));
   }
   public boolean tooManyBadAttempts(String username)
   {
      if (_badAttempts == null)
         _badAttempts = new HashMap<String, Integer>();
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
         HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech();
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
      if (hbmpersistence())
      {
         HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech();
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


   // == "authmgr" interface

   public void onLogin(String username, String password) { _fsm.onLogin(username, password); }
   public void onLogout() { _fsm.onLogout(); }

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

}
