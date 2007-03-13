package com.u2d.type.atom;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 13, 2007
 * Time: 1:32:35 PM
 */

import junit.framework.TestCase;

import java.util.Calendar;

public class TITest
      extends TestCase
{
   public void testCalc()
   {
      TimeInterval ti = new TimeInterval(Calendar.HOUR, 3);
      ti = ti.add(Calendar.MINUTE, 30);
      ti = ti.add(Calendar.SECOND, 23);
      assertEquals("03:30 hrs", ti.toHrMinFormat());
      assertEquals(3*3600*1000+30*60*1000+23*1000, ti.getMilis());
   }
}