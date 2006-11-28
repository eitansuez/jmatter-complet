/*
 * Created on Jan 20, 2004
 */
package com.u2d.core;

import com.u2d.pattern.OnionTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eitan Suez
 */
public class MyTestSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("Test for project p2");
      //$JUnit-BEGIN$
      suite.addTestSuite(FieldTest.class);
      suite.addTestSuite(ETypeTest.class);
      suite.addTestSuite(ValidationTest.class);
      suite.addTestSuite(ReflectionTest.class);
      suite.addTestSuite(CloneTest.class);
      suite.addTestSuite(CancelRestoreTest.class);
      suite.addTestSuite(OnionTest.class);
      suite.addTestSuite(MetaTest.class);
      //suite.addTestSuite(com.u2d.view.swing.PubSubTest.class);
      //$JUnit-END$
      return suite;
   }
}
