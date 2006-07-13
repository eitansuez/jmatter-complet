package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 10:52:24 AM
 */

import junit.framework.TestCase;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.ConditionStep;

public class ConditionTest extends TestCase
{
   public void testConditionTrue()
   {
      final MockBasicStep mock1 = new MockBasicStep();
      final MockBasicStep mock2 = new MockBasicStep();
      MockCondition condition = new MockCondition().instrument(true);
      ConditionStep cstep = new ConditionStep(mock1, mock2, condition);

      cstep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, mock2.invokeCount);

      Step nextStep = cstep.nextStep();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, mock2.invokeCount);

      nextStep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, mock2.invokeCount);

      assertNull(nextStep.nextStep());
   }

   public void testConditionFalse()
   {
      final MockBasicStep mock1 = new MockBasicStep();
      final MockBasicStep mock2 = new MockBasicStep();
      MockCondition condition = new MockCondition().instrument(false);
      ConditionStep cstep = new ConditionStep(mock1, mock2, condition);

      cstep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, mock2.invokeCount);

      assertNull(cstep.nextStep());
   }

   public void testConditionTrueNavBack()
   {
      final MockBasicStep mock1 = new MockBasicStep();
      final MockBasicStep mock2 = new MockBasicStep();
      MockCondition condition = new MockCondition().instrument(true);
      ConditionStep cstep = new ConditionStep(mock1, mock2, condition);

      cstep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, mock2.invokeCount);

      Step nextStep = cstep.nextStep();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, mock2.invokeCount);

      nextStep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, mock2.invokeCount);

      nextStep = nextStep.previousStep();
      nextStep.getView();
      assertEquals(2, mock1.invokeCount);
      assertEquals(1, mock2.invokeCount);
   }
}