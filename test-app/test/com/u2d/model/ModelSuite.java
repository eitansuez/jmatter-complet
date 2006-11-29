package com.u2d.model;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.u2d.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2006
 * Time: 12:36:38 PM
 */
public class ModelSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("JMatter Model Tests");
      //$JUnit-BEGIN$
      suite.addTestSuite(ComplexTypeTest.class);
      //$JUnit-END$
      return suite;
   }
}
