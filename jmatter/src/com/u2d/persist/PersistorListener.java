/*
 * Created on Mar 12, 2004
 */
package com.u2d.persist;

/**
 * @author Eitan Suez
 */
public interface PersistorListener
{
   public void onLoad();
   public void onBeforeCreate();
   public void onCreate();
   public void onBeforeSave();
   public void onSave();
   public void onDelete();
}
