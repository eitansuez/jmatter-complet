/*
 * Created on Apr 22, 2005
 */
package com.u2d.basic;

import junit.framework.TestCase;

/**
 * @author Eitan Suez
 */
public class StringTest extends TestCase
{
   public void testSplitHash()
   {
      String text = "eitan#suez";
      String[] parts = text.split("#");
      assertEquals("eitan", parts[0]);
      assertEquals("suez", parts[1]);
   }
   
   public void testSplitHash2()
   {
      String text = "com.u2d.clinic.Patient#created";
      String[] parts = text.split("#");
      assertEquals("com.u2d.clinic.Patient", parts[0]);
      assertEquals("created", parts[1]);
   }
   
   public void testSplitPipe()
   {
      String text = "one|two";
      String[] parts = text.split("\\|");
      assertEquals("one", parts[0]);
      assertEquals("two", parts[1]);
   }
   
}
