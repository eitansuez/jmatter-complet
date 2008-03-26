package com.u2d.type.atom;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Mar 26, 2008
 * Time: 1:55:14 PM
 */
public class TimeSpanParseTest extends TestCase
{
   public void testParseTimeSpan() throws java.text.ParseException
   {
      TimeSpan span = new TimeSpan();
      span.parseValue("03/30/2009 3:00 PM-4:00 PM");
      assertEquals("03/30/2009 3:00 PM-4:00 PM", span.toString());
   }
}
