package com.u2d.view.swing; /**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 8, 2007
 * Time: 1:07:48 PM
 */

public class SplashTest
{
   public static void main(String[] args)
   {
      Splash sp = new Splash();

      try
      {
         Thread.sleep(3000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }

      sp.dispose();
   }
}