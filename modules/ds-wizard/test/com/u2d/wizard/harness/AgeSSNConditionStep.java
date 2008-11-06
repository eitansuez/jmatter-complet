package com.u2d.wizard.harness;

import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.details.ConditionStep;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 9:17:25 AM
 */
public class AgeSSNConditionStep extends ConditionStep
{

   public AgeSSNConditionStep()
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

      init(ageStep, ssnStep, condition, null);
   }
}
