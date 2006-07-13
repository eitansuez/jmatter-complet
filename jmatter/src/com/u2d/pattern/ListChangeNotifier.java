/*
 * Created on Dec 10, 2003
 */
package com.u2d.pattern;

/**
 * @author Eitan Suez
 */
public interface ListChangeNotifier
{
   public void addListDataListener(javax.swing.event.ListDataListener l);
   public void removeListDataListener(javax.swing.event.ListDataListener l);
   
   public void fireContentsChanged(Object source, int index0, int index1);
   public void fireIntervalAdded(Object source, int index0, int index1);
   public void fireIntervalRemoved(Object source, int index0, int index1);
}
