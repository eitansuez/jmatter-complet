package com.u2d.view.swing;

import javax.swing.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 8, 2007
 * Time: 12:41:34 PM
 */
public class EntryPoint
{
   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            try {
               // feel free to replace with a different look and feel

               // making this explicit so will be default on platforms such as macosx,
               // where at the moment there are rendering issues with mac system look and feel
               UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception e) { }

            
            SwingViewMechanism.getInstance().launch();
         }
      });
   }

}
