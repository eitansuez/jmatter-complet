package com.u2d.app;

import com.u2d.pubsub.AppEventNotifier;
import com.u2d.pubsub.AppEventSupport;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEventType;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.element.EOCommand;
import com.u2d.element.Member;
import java.util.Map;
import java.util.HashMap;
import org.hibernate.Session;
import org.hibernate.Query;
import static com.u2d.pubsub.AppEventType.*;

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

   public AppSession() {}

   public void initialize()
   {
      _fsm = new AppSessionContext(this);
   }
   
   public void begin()
   {
      _vmech.setAppSession(this);
      _fsm.onBegin();
   }
   public void end()
   {
      _fsm.onEnd();
      _vmech.setAppSession(null);
      ((HBMPersistenceMechanism) pmech()).close();
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
         _user.liftRestrictions();
      }

      _user = user;

      if (_user!=null && hbmpersistence())
      {
         _user.applyRestrictions();
      }
   }
   
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
      Member.mergeInDbMetadata();
      
      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech();
      Session session = hbm.getSession();
      Query query = session.createQuery("from com.u2d.app.User as user where user.username = :username");
      query.setString("username", username);
      User user = (User) query.uniqueResult();
      user.onLoad();
      setUser(user);
      log(LoggedEvent.LOGIN, null, "Logged In");
      fireAppEventNotification(LOGIN);
   }

   public void clearUser()
   {
      log(LoggedEvent.LOGOUT,  null, "Logged Out");
      fireAppEventNotification(LOGOUT);
      setUser(null);
   }

   protected String _autologinas = null;
   public void setAutologinas(String username) { _autologinas = username; }
   public boolean autologin() { return _autologinas != null; }
   public String autologinusername() { return _autologinas; }

   public boolean authenticate(final String username, final String password)
   {
      return pmech().authenticate(username, password);
   }

   public void clearBadAttempts(String username)
   {
      if (_badAttempts == null)
         _badAttempts = new HashMap<String, Integer>();
      _badAttempts.put(username, 0);
   }
   public boolean tooManyBadAttempts(String username)
   {
      if (_badAttempts == null)
         _badAttempts = new HashMap<String, Integer>();
      Integer count = _badAttempts.get(username);
      if (count == null)
      {
         _badAttempts.put(username, 1);
         return false;
      }
      int num = count + 1;
      _badAttempts.put(username, num);
      return num >= THRESHOLD;
   }
   public boolean isLocked(String username)
   {
      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech();
      Session session = hbm.getSession();
      Query query = session.createQuery("select user.locked from com.u2d.app.User as user where user.username = :username");
      query.setString("username", username);
      BooleanEO islocked = (BooleanEO) query.uniqueResult();
      return islocked != null && islocked.booleanValue();
   }
   public void lock(final String username)
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


   // == "authmgr" interface

   public void onLogin(String username, String password) { _fsm.onLogin(username, password); }
   public void onLogout() { _fsm.onLogout(); }

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

}
