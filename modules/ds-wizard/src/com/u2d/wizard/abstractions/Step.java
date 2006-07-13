package com.u2d.wizard.abstractions;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:38:52 PM
 */
public interface Step
{
   public Step nextStep();
   public Step previousStep();

   public boolean hasNextStep();
   public boolean hasPreviousStep();

   public Step currentStep();
   
   public String title();
   public String description();
   public JComponent getView();
   
   public boolean viewDirty();

}
