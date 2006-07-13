package com.u2d.ui;

import java.awt.event.InputEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 29, 2005
 * Time: 1:08:34 PM
 */
public class Platform
{
   public static boolean APPLE =
         (System.getProperty("mrj.version") != null);

   public static int mask()
   {
      return (APPLE) ? InputEvent.META_MASK : InputEvent.CTRL_MASK;
   }
}
