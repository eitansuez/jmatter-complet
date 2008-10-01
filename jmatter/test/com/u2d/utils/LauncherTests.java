package com.u2d.utils;

import junit.framework.TestCase;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 29, 2008
 * Time: 9:00:43 AM
 */
public class LauncherTests extends TestCase
{
   public void testFileWithSpacesToString()
   {
      File file = new File("/home/eitan/my file.txt");
      Launcher.openFile(file);
   }
}
