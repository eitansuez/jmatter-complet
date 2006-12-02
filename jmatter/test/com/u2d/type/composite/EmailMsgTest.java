package com.u2d.type.composite;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 1, 2006
 * Time: 5:51:25 PM
 */

import junit.framework.TestCase;
import com.u2d.type.atom.StringEO;

public class EmailMsgTest
      extends TestCase
{
   public void testMailToURL()
   {
      Person p = new Person();
      p.getName().getFirst().setValue("Eitan");
      p.getName().getLast().setValue("Suez");
      p.getContact().getEmail().setValue("eitan@u2d.com");
      
      EmailMessage msg = new EmailMessage(p, new StringEO("Hello"));
      System.out.println(msg.mailtoURL());
      assertTrue(msg.mailtoURL().startsWith("mailto:"));
   }
}