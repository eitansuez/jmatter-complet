/*
 * Created on Jan 20, 2004
 */
package com.u2d.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Eitan Suez
 */
public class CoreSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("JMatter Core Tests");
      //$JUnit-BEGIN$
      suite.addTestSuite(FieldTest.class);
      suite.addTestSuite(ETypeTest.class);
//      suite.addTestSuite(ValidationTest.class);
//      suite.addTestSuite(ReflectionTest.class);
//      suite.addTestSuite(CloneTest.class);
//      suite.addTestSuite(CancelRestoreTest.class);
//      suite.addTestSuite(MetaTest.class);
//      suite.addTestSuite(com.u2d.view.swing.PubSubTest.class);
      //$JUnit-END$
      return suite;
   }
}
