package com.u2d.wizard.harness;

import com.u2d.wizard.details.CompositeStep;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:50:39 PM
 */
public class NameAgeSSNWizard extends CompositeStep
{

   public NameAgeSSNWizard()
   {
      _steps.add(new NameStep());
      _steps.add(new AgeSSNConditionStep());
      ready();
   }

   public String compositeTitle()
   {
      return "Test Wizard: Enter Name, Age, and [if applicable: ] SSN";
   }

}
