package com.u2d.utils;

import com.lowagie.tools.Executable;

import java.util.LinkedList;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 12, 2006
 * Time: 10:38:53 PM
 */
public class BrowserLauncher
{
   public static void openInBrowser(String url)
   {
      launch(browserCmds(url));
   }

   public static LinkedList<String> browserCmds(String url)
   {
      LinkedList<String> cmds = new LinkedList<String>();

      if (Executable.isLinux())
      {
         cmds.add(String.format("gnome-open %s", url));
         cmds.add(String.format("kfmclient openURL %s", url));
      }
      else if (Executable.isMac())
      {
         cmds.add(String.format("open %s", url));
      }
      else if (Executable.isWindows() && Executable.isWindows9X())
      {
         cmds.add(String.format("command.com /C start %s", url));
      }
      else if (Executable.isWindows())
      {
         cmds.add(String.format("cmd /c start %s", url));
      }
      return cmds;
   }

   public static void launch(LinkedList<String> cmds)
   {
      if (cmds.isEmpty())
      {
         System.err.println("Don't know how to execute command.");
         return;
      }
      
      String cmd = cmds.removeFirst();
      try
      {
         Runtime.getRuntime().exec(cmd);
      }
      catch (IOException ex)
      {
         if (cmds.size() > 0)
         {
            launch(cmds);
         }
         else
         {
            String msg = String.format("Failed to open url with command '%s'", cmd);
            System.err.println(msg);
            System.err.println(ex.getMessage());
            ex.printStackTrace();
         }
      }
   }

}
