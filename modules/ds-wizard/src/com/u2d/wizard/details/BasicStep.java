package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.Step;
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

   public String toString() { return title(); }
}
