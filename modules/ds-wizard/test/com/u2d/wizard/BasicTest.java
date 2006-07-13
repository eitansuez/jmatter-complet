package com.u2d.wizard;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 9:58:44 AM
 */

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;
import com.u2d.wizard.abstractions.ScriptEngine;
import com.u2d.wizard.details.NextAction;

public class BasicTest extends TestCase
{

   public void testBasic()
   {
      MockBasicStep mockStep = new MockBasicStep();

      List actions = new ArrayList();
      actions.add(NextAction.INSTANCE);
      new ScriptEngine(mockStep, actions).start();

      assertEquals(1, mockStep.invokeCount);
   }

   public void testNav()
   {
      MockBasicStep step = new MockBasicStep();

      step.getView();
      assertNull(step.nextStep());
      assertNull(step.previousStep());
   }

}