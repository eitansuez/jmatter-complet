package com.u2d.model;

import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationNotifier;

import javax.swing.*;
import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 2, 2009
 * Time: 10:34:20 AM
 */
public class WeakValidationListener extends WeakReference<ValidationListener> implements ValidationListener
{
   private ValidationNotifier _notifier;
   public WeakValidationListener(ValidationListener referent, ValidationNotifier notifier)
   {
      super(referent);
      _notifier = notifier;
   }

   public void validationException(ValidationEvent evt)
   {
      ValidationListener referent = get();
      if (referent == null)
      {
         System.out.println("removing validation listener for reclaimed object..");
         _notifier.removeValidationListener(this);
      }
      else
      {
         referent.validationException(evt);
      }
   }

   public static ValidationListener wrap(ValidationListener l, ValidationNotifier n)
   {
      if (l instanceof JComponent)
      {
         return new WeakValidationListener(l, n);
      }
      else
      {
         return l;
      }
   }
}