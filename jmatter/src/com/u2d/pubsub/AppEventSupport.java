/*
 * Created on Mar 26, 2004
 */
package com.u2d.pubsub;

import java.util.*;

/**
 * @author Eitan Suez
 */
public class AppEventSupport implements AppEventNotifier
{
   AppEventNotifier _source;
   
   public AppEventSupport(AppEventNotifier source)
   {
      _source = source;
   }
   
   Map listeners = new HashMap();
   
   public synchronized void addAppEventListener(String evtType, AppEventListener l)
   {
      if (listeners.get(evtType) == null)
      {
         listeners.put(evtType, new HashSet());
      }
      Set set = (Set) listeners.get(evtType);
      set.add(l);
   }
   
   public synchronized void removeAppEventListener(String evtType, AppEventListener l)
   {
      if (listeners.get(evtType) == null) return;
      Set set = (Set) listeners.get(evtType);
      set.remove(l);
   }
   
   public void fireAppEventNotification(String evtType)
   {
      fireAppEventNotification(evtType, null);
   }
   
   public void fireAppEventNotification(String evtType, Object target)
   {
      if (listeners.get(evtType) == null) return;
      synchronized(this)
      {
         HashSet set = (HashSet) listeners.get(evtType);
         Set targets = (Set) set.clone();
         AppEventListener listener = null;
         AppEvent evt = new AppEvent(_source, evtType, target);
         Iterator itr = targets.iterator();
         while (itr.hasNext())
         {
            listener = (AppEventListener) itr.next();
            listener.onEvent(evt);
         }
      }
   }
   
}
