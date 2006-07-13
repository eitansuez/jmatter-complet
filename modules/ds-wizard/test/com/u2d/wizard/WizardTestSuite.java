package com.u2d.wizard;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 12:51:03 PM
 */
public class WizardTestSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("Wizard Tests");
      suite.addTestSuite(BasicTest.class);
      suite.addTestSuite(CompositeTest.class);
      suite.addTestSuite(CompositeRealTest.class);
      suite.addTestSuite(ConditionTest.class);
      suite.addTestSuite(NestedConditionTest.class);
      suite.addTestSuite(CommitTest.class);
      suite.addTestSuite(NumberingTest.class);
      return suite;
   }

}
