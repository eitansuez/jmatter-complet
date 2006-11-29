package com.u2d.restrict;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2006
 * Time: 1:35:31 PM
 */
public class RestrictSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("JMatter Tests (most are in separate project test-app)");
      //$JUnit-BEGIN$
      suite.addTestSuite(CmdRestrictTest.class);
      //$JUnit-END$
      return suite;
   }
}
