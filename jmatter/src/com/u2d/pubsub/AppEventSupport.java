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
   
   Map<AppEventType, HashSet<AppEventListener>> listeners = new HashMap<AppEventType, HashSet<AppEventListener>>();
   
   public synchronized void addAppEventListener(AppEventType evtType, AppEventListener l)
   {
      if (listeners.get(evtType) == null)
      {
         listeners.put(evtType, new HashSet<AppEventListener>());
      }
      Set<AppEventListener> set = listeners.get(evtType);
      set.add(l);
   }
   
   public synchronized void removeAppEventListener(AppEventType evtType, AppEventListener l)
   {
      if (listeners.get(evtType) == null) return;
      Set set = listeners.get(evtType);
      set.remove(l);
   }
   
   public void fireAppEventNotification(AppEventType evtType)
   {
      fireAppEventNotification(evtType, null);
   }
   
   public void fireAppEventNotification(AppEventType evtType, Object target)
   {
      if (listeners.get(evtType) == null) return;
      synchronized(this)
      {
         HashSet set = listeners.get(evtType);
         Set targets = (Set) set.clone();
         AppEvent evt = new AppEvent(_source, evtType, target);
         for (Iterator itr = targets.iterator(); itr.hasNext(); )
         {
            AppEventListener listener = (AppEventListener) itr.next();
            listener.onEvent(evt);
         }
      }
   }
   
   public int getListenerCount() { return listeners.size(); }
   
}
