package com.u2d.tools;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 27, 2006
 * Time: 5:36:26 PM
 */

import junit.framework.TestCase;

public class PropertySetterTests
      extends TestCase
{
   public void testMatch()
   {
      String file = "my-postgresql-8.1.4-3.jar";
      assertTrue(file.contains("postgres"));
      assertFalse(file.contains("nonsense"));
   }
}