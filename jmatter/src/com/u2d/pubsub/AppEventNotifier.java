/*
 * Created on Mar 26, 2004
 */
package com.u2d.pubsub;

/**
 * @author Eitan Suez
 */
public interface AppEventNotifier
{
   public void addAppEventListener(AppEventType evtType, AppEventListener l);
   public void removeAppEventListener(AppEventType evtType, AppEventListener l);
   public void fireAppEventNotification(AppEventType evtType);
}
