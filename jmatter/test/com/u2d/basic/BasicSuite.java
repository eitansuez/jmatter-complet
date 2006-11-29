package com.u2d.basic;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2006
 * Time: 1:34:21 PM
 */
public class BasicSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("JMatter Tests (most are in separate project test-app)");
      //$JUnit-BEGIN$
      suite.addTestSuite(CharTest.class);
      suite.addTestSuite(StringTest.class);
      //$JUnit-END$
      return suite;
   }
}
