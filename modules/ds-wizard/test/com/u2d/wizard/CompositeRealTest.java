package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 11:44:05 AM
 */

import junit.framework.TestCase;
import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.abstractions.ScriptEngine;
import com.u2d.wizard.details.NextAction;
import com.u2d.wizard.details.NotCondition;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.ConditionStep;

public class CompositeRealTest extends TestCase
{
   public void testPatientWizardStructure()
   {
      Step step = setupWizard(false, true, false, true);

      // do a basic visual check first:
      new ScriptEngine(step, NextAction.times(100)).start();
      System.out.println("----------");

      // try alternative conditional permutations
      // 1 turn hasrefcondition on
      step = setupWizard(true, true, false, true);
      new ScriptEngine(step, NextAction.times(100)).start();
      System.out.println("----------");

      // 2 make insured not self (so enter insured's person info)
      step = setupWizard(false, false, false, true);
      new ScriptEngine(step, NextAction.times(100)).start();
      System.out.println("----------");

      // 3 has secondary insurance info
      step = setupWizard(false, true, true, true);
      new ScriptEngine(step, NextAction.times(100)).start();
      System.out.println("----------");

      // 4 has secondary insurance info and secondary insured is not self
      step = setupWizard(false, true, true, false);
      new ScriptEngine(step, NextAction.times(100)).start();
      System.out.println("----------");

      // 5 - turn all conditions on
      step = setupWizard(true, false, true, false);
      new ScriptEngine(step, NextAction.times(100)).start();
      System.out.println("----------");
   }

   public Step setupWizard(boolean hasRefBool,
                           boolean insSelfBool,
                           boolean hasSecondInsBool,
                           boolean secondInsSelfBool)
   {
      MockBasicStep patientInfo = new MockBasicStep("Patient Information");
      MockBasicStep emergencyInfo = new MockBasicStep("Emergency Information");

      MockBasicStep hasRefPhysician = new MockBasicStep("Were you referred to the clinic?");
      MockBasicStep refPhysician = new MockBasicStep("Referring Physician Information");
      MockCondition condition = new MockCondition().instrument(hasRefBool);

      ConditionStep refPhysCondStep = new ConditionStep(hasRefPhysician, refPhysician, condition);

      Step insuranceStep = insuranceStep(insSelfBool);
      Step secondaryInsuranceStep = insuranceStep(secondInsSelfBool);

      MockBasicStep hasSecondaryInsurance = new MockBasicStep("Do you have secondary insurance?");
      Condition hasSecondaryCondition = new MockCondition().instrument(hasSecondInsBool);
      ConditionStep secondaryInsCondStep = new ConditionStep(hasSecondaryInsurance,
                                                             secondaryInsuranceStep,
                                                             hasSecondaryCondition);

      MockBasicStep lastStep = new MockBasicStep("Last Step");

      CompositeStep wizard = new CompositeStep();
      wizard.addStep(patientInfo);
      wizard.addStep(emergencyInfo);
      wizard.addStep(refPhysCondStep);
      wizard.addStep(insuranceStep);
      wizard.addStep(secondaryInsCondStep);
      wizard.addStep(lastStep);
      wizard.ready();

      return wizard;

   }

   private Step insuranceStep(boolean insuredIsSelfBool)
   {
      Step insuranceRelation = new MockBasicStep("What is insured's relation to the patient?");
      Step insurancePersonInfo = new MockBasicStep("Enter Insured Person Basic Information");
      MockCondition relationIsSelf = new MockCondition().instrument(insuredIsSelfBool);
      ConditionStep isSelfCondStep = new ConditionStep(insuranceRelation, insurancePersonInfo,
                                                       new NotCondition(relationIsSelf));

      Step insuranceInfo = new MockBasicStep("Enter Insured Person's Insurance Information");

      CompositeStep insuranceStep = new CompositeStep();
      insuranceStep.addStep(isSelfCondStep);
      insuranceStep.addStep(insuranceInfo);
      insuranceStep.ready();
      return insuranceStep;
   }
}