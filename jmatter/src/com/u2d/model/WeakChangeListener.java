package com.u2d.model;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 2, 2009
 * Time: 10:34:09 AM
 */
public class WeakChangeListener extends WeakReference<ChangeListener> implements ChangeListener
{
   private ChangeNotifier _notifier;
   public WeakChangeListener(ChangeListener referent, ChangeNotifier notifier)
   {
      super(referent);
      _notifier = notifier;
   }

   public void stateChanged(ChangeEvent e)
   {
      ChangeListener referent = get();
      if (referent == null)
      {
         System.out.println("removing change listener for reclaimed object..");
         _notifier.removeChangeListener(this);
      }
      else
      {
         referent.stateChanged(e);
      }
   }

   public static ChangeListener wrap(ChangeListener l, ChangeNotifier n)
   {
      if (l instanceof JComponent)
      {
         return new WeakChangeListener(l, n);
      }
      else
      {
         return l;
      }
   }
}
