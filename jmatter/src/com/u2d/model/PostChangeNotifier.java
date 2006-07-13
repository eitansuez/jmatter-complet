package com.u2d.model;

import javax.swing.event.ChangeListener;

/**
 * @author Eitan Suez
 */
public interface PostChangeNotifier
{
   public void addPostChangeListener(ChangeListener l);
   public void removePostChangeListener(ChangeListener l);
   public void fireStateChanged();
}
