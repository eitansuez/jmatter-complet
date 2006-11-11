package com.u2d.basic;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 10, 2006
 * Time: 3:51:33 PM
 */
public class CharTest extends TestCase
{
   char achar;
   
   public void testDefaultValue()
   {
      System.out.println("Code: "+((int) achar));
      assertTrue(Character.isDefined(achar));
      assertFalse(Character.isWhitespace(achar));
   }
   
   public void testEOSValue()
   {
      char someChar = '\0';
      assertEquals(0, ((int) someChar));
   }
}
