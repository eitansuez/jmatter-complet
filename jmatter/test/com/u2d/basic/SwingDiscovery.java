/*
 * Created on Jan 28, 2004
 */
package com.u2d.basic;

import junit.framework.TestCase;
import java.util.*;

import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class SwingDiscovery extends TestCase
{
   
   public void testUIManagerDefaults()
   {
      UIDefaults defaults = UIManager.getDefaults();
      Enumeration keys = defaults.keys();
      System.out.println("UIManager UIDefaults Keys:");
      while (keys.hasMoreElements())
      {
         Object key = keys.nextElement();
         System.out.println(key.toString());
      }
   }
   
}
