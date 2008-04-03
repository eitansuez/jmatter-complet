package com.jmatter.synergy;

import junit.framework.TestCase;
import com.u2d.type.atom.TimeSpan;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 3, 2008
 * Time: 2:20:52 PM
 */
public class TimeSpanTest extends TestCase
{
   public void testTime()
   {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DAY_OF_MONTH, 4);
      cal.set(Calendar.MONTH, 3);
      cal.set(Calendar.HOUR_OF_DAY, 15);
      cal.set(Calendar.MINUTE, 30);
      cal.set(Calendar.SECOND, 0);

      TimeSpan span = new TimeSpan();
      Date startDate = cal.getTime();
      span.startDate(startDate);
      cal.add(Calendar.HOUR, 2);
      Date endDate = cal.getTime();
      span.endDate(endDate);

      assertEquals(span.startDate(), startDate);
      assertEquals(span.endDate(), endDate);
   }
}
