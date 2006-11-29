package com.u2d.pattern;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2006
 * Time: 12:27:54 PM
 */
public class PatternSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("JMatter Tests (most are in separate project test-app)");
      //$JUnit-BEGIN$
      suite.addTestSuite(OnionTest.class);
      //$JUnit-END$
      return suite;
   }
}
