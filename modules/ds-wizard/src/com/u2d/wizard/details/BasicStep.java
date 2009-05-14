package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.Step;
import com.u2d.validation.ValidationNotifier;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:39:01 PM
 */
public abstract class BasicStep implements Step
{
   public Step nextStep() { return null; }
   public Step previousStep() { return null; }

   public Step currentStep() { return this; }

   public boolean hasNextStep() { return false; }
   public boolean hasPreviousStep() { return false; }

   public abstract String title();
   public abstract String description();
   public abstract JComponent getView();

   public boolean viewDirty() { return false; }

   /**
    * @param notifier use this object to send validation messages to the ui
    * @return number of validation errors (should return 0 if validation passed)
    */
   public int validate(ValidationNotifier notifier)
   {
      return 0; // default implementation.  overriding optional.
   }

   public String toString() { return title(); }
}
