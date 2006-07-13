package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 26, 2005
 * Time: 6:01:44 PM
 */

import junit.framework.TestCase;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.BasicStep;
import com.u2d.wizard.details.CommitStep;
import com.u2d.wizard.details.Wizard;
import com.u2d.wizard.abstractions.Step;

import javax.swing.*;

public class CommitTest extends TestCase
{

   public void testCommit()
   {
      CompositeStep ct = new CompositeStep("testing");
      BasicStep mock1 = new MockBasicStep();
      BasicStep mock2 = new MockBasicStep();
      CommitStep third = new CommitStep()
      {
         public void commit()
         {
            System.out.println("ok, committing..");
         }

         public JComponent getView() { return null; }
         public String title() { return "the commit step"; }
         public String description() { return "the commit step"; }
      };
      BasicStep mock3 = new MockBasicStep();

      ct.addStep(mock1);
      ct.addStep(mock2);
      ct.addStep(third);
      ct.addStep(mock3);

      ct.ready();

      Step step = ct;

      step.getView();
      step = step.nextStep();

      step.getView();
      step = step.nextStep();

      step.getView();
      step = step.nextStep();

      assertFalse(step.hasPreviousStep());

      Step shouldBeSame = step.previousStep();
      assertSame(shouldBeSame, step);

   }

   public void testCommitNestedComposite()
   {
      CompositeStep ct = new CompositeStep("testing");
      BasicStep mock1 = new MockBasicStep();
      CommitStep second = new CommitStep()
      {
         public void commit()
         {
            System.out.println("ok, committing..");
         }

         public JComponent getView() { return null; }
         public String title() { return "the commit step"; }
         public String description() { return "the commit step"; }
      };
      ct.addStep(mock1);
      ct.addStep(second);

      ct.ready();

      Wizard wiz = new Wizard(ct);
      wiz.addStep(new MockBasicStep());

      Step step = wiz;

      step.getView();  // start
      step = step.nextStep();

      step.getView();  // mock1
      step = step.nextStep();

      step.getView();  // second (commit)
      step = step.nextStep();
      
      step.getView(); // last step (a mock basic step)

      // ensure that you cannot go back after you've crossed
      // a commit step:
      assertFalse(step.hasPreviousStep());

      Step shouldBeSame = step.previousStep();
      assertSame(shouldBeSame, step);
   }
}