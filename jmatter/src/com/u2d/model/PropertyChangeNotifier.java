/*
 * Created on Mar 2, 2005
 */
package com.u2d.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Eitan Suez
 */
public interface PropertyChangeNotifier
{
   public void addPropertyChangeListener(PropertyChangeListener listener);
   public void addPropertyChangeListener(String propertyName, 
         PropertyChangeListener listener);
   
   public void removePropertyChangeListener(PropertyChangeListener listener);
   public void removePropertyChangeListener(String propertyName, 
         PropertyChangeListener listener);

   public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
   public void firePropertyChange(PropertyChangeEvent event);
   public void firePropertyChange(String propertyName, int oldValue, int newValue);
   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue);

}
