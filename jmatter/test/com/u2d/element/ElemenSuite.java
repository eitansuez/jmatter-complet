package com.u2d.element;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.u2d.pattern.OnionTest;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2006
 * Time: 1:32:47 PM
 */
public class ElemenSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("JMatter Tests (most are in separate project test-app)");
      //$JUnit-BEGIN$
      suite.addTestSuite(CommandTest.class);
      //$JUnit-END$
      return suite;
   }
}
