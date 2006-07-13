/*
 * Created on Mar 2, 2005
 */
package com.u2d.model;

import javax.swing.event.ChangeListener;

/**
 * @author Eitan Suez
 */
public interface ChangeNotifier
{
   public void addChangeListener(ChangeListener l);
   public void removeChangeListener(ChangeListener l);
   public void fireStateChanged();
}
