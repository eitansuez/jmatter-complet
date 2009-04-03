package com.u2d.model;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 2, 2009
 * Time: 10:34:33 AM
 */
public class WeakPropertyChangeListener extends WeakReference<PropertyChangeListener> implements PropertyChangeListener
{
   private PropertyChangeNotifier _notifier;
   public WeakPropertyChangeListener(PropertyChangeListener referent, PropertyChangeNotifier notifier)
   {
      super(referent);
      _notifier = notifier;
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      PropertyChangeListener referent = get();
      if (referent == null)
      {
         System.out.println("removing propertychange listener for reclaimed object..");
         _notifier.removePropertyChangeListener(this);
      }
      else
      {
         referent.propertyChange(evt);
      }
   }

   public static PropertyChangeListener wrap(PropertyChangeListener l, PropertyChangeNotifier n)
   {
      if (l instanceof JComponent)
      {
         return new WeakPropertyChangeListener(l, n);
      }
      else
      {
         return l;
      }
   }
}
