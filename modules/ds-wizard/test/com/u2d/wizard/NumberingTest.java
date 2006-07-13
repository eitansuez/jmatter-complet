package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 2, 2005
 * Time: 2:14:02 PM
 */

import junit.framework.TestCase;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.Wizard;
import com.u2d.wizard.abstractions.Step;

public class NumberingTest extends TestCase
{
   public void testSimpleNumbering()
   {
      MockBasicStep mock1 = new MockBasicStep();
      MockBasicStep mock2 = new MockBasicStep();
      MockBasicStep mock3 = new MockBasicStep();

      CompositeStep composite = new CompositeStep();
      composite.addStep(mock1);
      composite.addStep(mock2);
      composite.addStep(mock3);
      composite.ready();

      Step nextStep = composite;

      nextStep.getView();
      assertEquals(1, composite.stepNumber());
      nextStep = nextStep.nextStep();

      nextStep.getView();
      assertEquals(2, composite.stepNumber());
      nextStep = nextStep.nextStep();

      nextStep.getView();
      assertEquals(3, composite.stepNumber());

      assertFalse(nextStep.hasNextStep());
   }

   public void testWizardNumbering()
   {
      MockBasicStep mock1 = new MockBasicStep();
      MockBasicStep mock2 = new MockBasicStep();
      MockBasicStep mock3 = new MockBasicStep();

      CompositeStep composite = new CompositeStep();
      composite.addStep(mock1);
      composite.addStep(mock2);
      composite.addStep(mock3);
      composite.ready();

      Wizard wizard = new Wizard(composite);

      Step step = wizard;

      step.getView();
      assertEquals(0, wizard.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(1, wizard.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(2, wizard.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(0, wizard.stepNumber());

      assertFalse(step.hasNextStep());
   }

   public void testNestedCompositeNumbering()
   {
      MockBasicStep mock1 = new MockBasicStep();
      MockBasicStep mock2 = new MockBasicStep();
      MockBasicStep mock3 = new MockBasicStep();

      CompositeStep compb = new CompositeStep();
      compb.addStep(mock1);
      compb.addStep(mock2);
      compb.addStep(mock3);
      compb.ready();

      MockBasicStep mocka = new MockBasicStep();
      MockBasicStep mockc = new MockBasicStep();

      CompositeStep topstep = new CompositeStep();
      topstep.addStep(mocka);
      topstep.addStep(compb);
      topstep.addStep(mockc);
      topstep.ready();


      Step step = topstep;

      step.getView();
      assertEquals(1, topstep.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(2, topstep.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(3, topstep.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(4, topstep.stepNumber());
      step = step.nextStep();

      step.getView();
      assertEquals(5, topstep.stepNumber());

      assertFalse(step.hasNextStep());
   }

}