package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 11:10:38 AM
 */

import junit.framework.TestCase;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.CompositeStep;

public class CompositeTest extends TestCase
{

   public void testSimpleComposite()
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
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, mock2.invokeCount);
      assertEquals(0, mock3.invokeCount);

      nextStep = nextStep.nextStep();
      nextStep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, mock2.invokeCount);
      assertEquals(0, mock3.invokeCount);

      nextStep = nextStep.nextStep();
      nextStep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, mock2.invokeCount);
      assertEquals(1, mock3.invokeCount);

      nextStep = nextStep.nextStep();
      assertNull(nextStep);
   }

   public void testSimpleCompositeBackNav()
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
      nextStep.getView(); nextStep = nextStep.nextStep();
      nextStep.getView(); nextStep = nextStep.nextStep();
      nextStep.getView();
      nextStep = nextStep.previousStep();
      nextStep.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(2, mock2.invokeCount);
      assertEquals(1, mock3.invokeCount);

      nextStep = nextStep.previousStep();
      nextStep.getView();
      assertEquals(2, mock1.invokeCount);
      assertEquals(2, mock2.invokeCount);
      assertEquals(1, mock3.invokeCount);

      nextStep = nextStep.previousStep();
      assertNull(nextStep);
   }

   public void testNestedCompositeStep()
   {
      MockBasicStep mock1 = new MockBasicStep();

      MockBasicStep nested1 = new MockBasicStep();
      MockBasicStep nested2 = new MockBasicStep();
      MockBasicStep nested3 = new MockBasicStep();
      CompositeStep mock2 = new CompositeStep();
      mock2.addStep(nested1);
      mock2.addStep(nested2);
      mock2.addStep(nested3);
      mock2.ready();

      MockBasicStep mock3 = new MockBasicStep();

      CompositeStep composite = new CompositeStep();
      composite.addStep(mock1);
      composite.addStep(mock2);
      composite.addStep(mock3);
      composite.ready();

      Step step = composite;
      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, nested1.invokeCount);
      assertEquals(0, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, nested1.invokeCount);
      assertEquals(0, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(1, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

      step = step.nextStep();
      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(1, nested3.invokeCount);
      assertEquals(1, mock3.invokeCount);

      step = step.nextStep();
      assertNull(step);
   }

   public void testNestedCompositeStepBackAndForNav()
   {
      MockBasicStep mock1 = new MockBasicStep();

      MockBasicStep nested1 = new MockBasicStep();
      MockBasicStep nested2 = new MockBasicStep();
      MockBasicStep nested3 = new MockBasicStep();
      CompositeStep mock2 = new CompositeStep();
      mock2.addStep(nested1);
      mock2.addStep(nested2);
      mock2.addStep(nested3);
      mock2.ready();

      MockBasicStep mock3 = new MockBasicStep();

      CompositeStep composite = new CompositeStep();
      composite.addStep(mock1);
      composite.addStep(mock2);
      composite.addStep(mock3);
      composite.ready();

      Step step = composite;

      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(0, nested1.invokeCount);
      assertEquals(0, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

      step = step.nextStep();  // forward

      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, nested1.invokeCount);
      assertEquals(0, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.nextStep();

      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(1, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

      step = step.previousStep();  // back

      step.getView();
      assertEquals(1, mock1.invokeCount);
      assertEquals(2, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.previousStep();

      //  now am where i started

      step.getView();
      assertEquals(2, mock1.invokeCount);
      assertEquals(2, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.nextStep();  // forward again

      step.getView();
      assertEquals(2, mock1.invokeCount);
      assertEquals(3, nested1.invokeCount);
      assertEquals(1, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.nextStep();

      step.getView();
      assertEquals(2, mock1.invokeCount);
      assertEquals(3, nested1.invokeCount);
      assertEquals(2, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.previousStep();  // back again

      step.getView();
      assertEquals(2, mock1.invokeCount);
      assertEquals(4, nested1.invokeCount);
      assertEquals(2, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.previousStep();

      step.getView();
      assertEquals(3, mock1.invokeCount);
      assertEquals(4, nested1.invokeCount);
      assertEquals(2, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);
      step = step.nextStep();  // finally, forward one more time

      step.getView();
      assertEquals(3, mock1.invokeCount);
      assertEquals(5, nested1.invokeCount);
      assertEquals(2, nested2.invokeCount);
      assertEquals(0, nested3.invokeCount);
      assertEquals(0, mock3.invokeCount);

   }

   public void testCompositePreviousNullInitial()
   {
      MockBasicStep mock1 = new MockBasicStep();
      MockBasicStep mock2 = new MockBasicStep();
      MockBasicStep mock3 = new MockBasicStep();

      CompositeStep composite = new CompositeStep();
      composite.addStep(mock1);
      composite.addStep(mock2);
      composite.addStep(mock3);
      composite.ready();

      Step step = composite;

      step.getView();
      assertFalse(step.hasPreviousStep());
   }


}