package com.u2d.wizard.harness;

import com.u2d.wizard.details.NextAction;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.ConditionStep;
import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.abstractions.ScriptEngine;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 6:51:57 PM
 */
public class Main
{
   public static void main(String[] args)
   {
      new Main();
   }

   public Main()
   {
      List steps = new ArrayList();
      steps.add(new NameStep());
      steps.add(ageSSNConditionStep());
      Step wizard = new CompositeStep(steps);

      new ScriptEngine(wizard, NextAction.times(3)).start();
   }

   private Step ageSSNConditionStep()
   {
      final AgeStep ageStep = new AgeStep();
      final SSNStep ssnStep = new SSNStep();
      Condition condition = new Condition()
      {
         public boolean evaluate()
         {
            return (ageStep.getAge() > 18);
         }
      };
      return new ConditionStep(ageStep, ssnStep, condition);
   }

}
