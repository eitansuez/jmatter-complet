package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 1:01:01 PM
 */

import junit.framework.TestCase;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.ConditionStep;

public class NestedConditionTest extends TestCase
{
   public void testNestedConditionTrueTrue()
   {
      MockBasicStep substep1 = new MockBasicStep();
      MockBasicStep substep2 = new MockBasicStep();

      MockCondition nestedCondition = new MockCondition().instrument(true);
      ConditionStep nestedConditionStep = new ConditionStep(substep1, substep2, nestedCondition);

      MockBasicStep step1 = new MockBasicStep();
      MockCondition mainCondition = new MockCondition().instrument(true);
      ConditionStep conditionStep = new ConditionStep(step1, nestedConditionStep, mainCondition);

      Step step = conditionStep;

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(0, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(1, substep2.invokeCount);

      step = step.previousStep();
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(2, substep1.invokeCount);
      assertEquals(1, substep2.invokeCount);
   }

   public void testNestedConditionFalseTrue()
   {
      MockBasicStep substep1 = new MockBasicStep();
      MockBasicStep substep2 = new MockBasicStep();

      MockCondition nestedCondition = new MockCondition().instrument(true);
      ConditionStep nestedConditionStep = new ConditionStep(substep1, substep2, nestedCondition);

      MockBasicStep step1 = new MockBasicStep();
      MockCondition mainCondition = new MockCondition().instrument(false);
      ConditionStep conditionStep = new ConditionStep(step1, nestedConditionStep, mainCondition);

      Step step = conditionStep;

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(0, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);

      step = step.nextStep();
      assertNull(step);
   }

   public void testNestedConditionFalseFalse()
   {
      MockBasicStep substep1 = new MockBasicStep();
      MockBasicStep substep2 = new MockBasicStep();

      MockCondition nestedCondition = new MockCondition().instrument(false);
      ConditionStep nestedConditionStep = new ConditionStep(substep1, substep2, nestedCondition);

      MockBasicStep step1 = new MockBasicStep();
      MockCondition mainCondition = new MockCondition().instrument(false);
      ConditionStep conditionStep = new ConditionStep(step1, nestedConditionStep, mainCondition);

      Step step = conditionStep;

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(0, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);

      step = step.nextStep();
      assertNull(step);
   }

   public void testNestedConditionTrueFalse()
   {
      MockBasicStep substep1 = new MockBasicStep();
      MockBasicStep substep2 = new MockBasicStep();

      MockCondition nestedCondition = new MockCondition().instrument(false);
      ConditionStep nestedConditionStep = new ConditionStep(substep1, substep2, nestedCondition);

      MockBasicStep step1 = new MockBasicStep();
      MockCondition mainCondition = new MockCondition().instrument(true);
      ConditionStep conditionStep = new ConditionStep(step1, nestedConditionStep, mainCondition);

      Step step = conditionStep;

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(0, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);

      step = step.nextStep();
      assertNull(step);
   }

   public void testConditionCompositeChild()
   {
      MockBasicStep step1 = new MockBasicStep("step1/condition");
      CompositeStep step2 = new CompositeStep();
      MockBasicStep substep1 = new MockBasicStep("substep1");
      MockBasicStep substep2 = new MockBasicStep("substep2");
      MockBasicStep substep3 = new MockBasicStep("substep3");
      step2.addStep(substep1);
      step2.addStep(substep2);
      step2.addStep(substep3);
      step2.ready();

      MockCondition condition = new MockCondition().instrument(true);
      ConditionStep cstep = new ConditionStep(step1, step2, condition);

      Step step = cstep;

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(0, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);
      assertEquals(0, substep3.invokeCount);

      step = step.nextStep();
      assertNotNull(step);

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);
      assertEquals(0, substep3.invokeCount);

      step = step.nextStep();
      assertNotNull(step);
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(1, substep2.invokeCount);
      assertEquals(0, substep3.invokeCount);

      step = step.nextStep();
      assertNotNull(step);
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(1, substep2.invokeCount);
      assertEquals(1, substep3.invokeCount);

      step = step.nextStep();
      assertNull(step);
   }

   public void testConditionCompositeChildWithBackNav()
   {
      MockBasicStep step1 = new MockBasicStep("step1/condition");
      CompositeStep step2 = new CompositeStep();
      MockBasicStep substep1 = new MockBasicStep("substep1");
      MockBasicStep substep2 = new MockBasicStep("substep2");
      MockBasicStep substep3 = new MockBasicStep("substep3");
      step2.addStep(substep1);
      step2.addStep(substep2);
      step2.addStep(substep3);
      step2.ready();

      MockCondition condition = new MockCondition().instrument(true);
      ConditionStep cstep = new ConditionStep(step1, step2, condition);

      Step step = cstep;

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(0, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);
      assertEquals(0, substep3.invokeCount);

      step = step.nextStep();
      assertNotNull(step);

      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(0, substep2.invokeCount);
      assertEquals(0, substep3.invokeCount);

      step = step.nextStep();
      assertNotNull(step);
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(1, substep2.invokeCount);
      assertEquals(0, substep3.invokeCount);

      step = step.nextStep();
      assertNotNull(step);
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(1, substep2.invokeCount);
      assertEquals(1, substep3.invokeCount);

      step = step.previousStep();
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(1, substep1.invokeCount);
      assertEquals(2, substep2.invokeCount);
      assertEquals(1, substep3.invokeCount);

      step = step.previousStep();
      step.getView();
      assertEquals(1, step1.invokeCount);
      assertEquals(2, substep1.invokeCount);
      assertEquals(2, substep2.invokeCount);
      assertEquals(1, substep3.invokeCount);

      step = step.previousStep();
      step.getView();
      assertEquals(2, step1.invokeCount);
      assertEquals(2, substep1.invokeCount);
      assertEquals(2, substep2.invokeCount);
      assertEquals(1, substep3.invokeCount);

      // what is the step? is it the condition step?
      assertSame(step, cstep);
      // now change the condition..
      condition.instrument(false);
      step = step.nextStep();
      assertNull(step);
   }

}