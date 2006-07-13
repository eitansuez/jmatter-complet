/*
 * Created on Mar 10, 2004
 */
package com.u2d.ui.lf;

/**
 * @author Eitan Suez
 */
public interface LFChangeNotifier
{
   public void addLFChangeListener(LFChangeListener l);
   public void removeLFChangeListener(LFChangeListener l);
   public void fireLFChanged();
}
