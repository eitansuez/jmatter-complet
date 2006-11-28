package com.u2d.model;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 8, 2006
 * Time: 11:34:02 AM
 */

import junit.framework.TestCase;
import com.u2d.domain.Order;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TimeSpan;

public class ComplexTypeTest
      extends TestCase
{

   public void testHasFieldType()
   {
      ComplexType orderType = ComplexType.forClass(Order.class);
      assertTrue(orderType.hasFieldOfType(StringEO.class));
      assertFalse(orderType.hasFieldOfType(TimeSpan.class));
   }

}