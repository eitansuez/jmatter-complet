/*
 * Created on Apr 1, 2005
 */
package com.u2d.ui.lf;

import javax.swing.event.EventListenerList;

/**
 * @author Eitan Suez
 */
public class LFChangeSupport implements LFChangeNotifier
{
   protected transient LFChangeEvent changeEvent = null;
   protected transient EventListenerList _listenerList = new EventListenerList();

   public void addLFChangeListener(LFChangeListener l)
   {
      _listenerList.add(LFChangeListener.class, l);
   }

   public void removeLFChangeListener(LFChangeListener l)
   {
      _listenerList.remove(LFChangeListener.class, l);
   }

   public void fireLFChanged()
   {
      Object[] listeners = _listenerList.getListenerList();
      
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i]==LFChangeListener.class)
         {
            if (changeEvent == null)
               changeEvent = new LFChangeEvent(this);
            ((LFChangeListener)listeners[i+1]).LFChanged(changeEvent);
         }
      }
   }

}

