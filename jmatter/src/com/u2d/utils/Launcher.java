package com.u2d.utils;

import com.lowagie.tools.Executable;
import com.u2d.type.composite.EmailMessage;

import java.util.LinkedList;
import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 12, 2006
 * Time: 10:38:53 PM
 */
public class Launcher
{
   public static void openFile(File file)
   {
      openFile(file.getAbsolutePath());
   }

   public static void openFile(String fileName)
   {
      launch(execCmds(fileName));
   }

   public static void openInBrowser(String url)
   {
      launch(browserCmds(url));
   }
   
   public static void openInEmailApp(EmailMessage msg)
   {
      openInEmailApp(msg.mailtoURL());
   }
   public static void openInEmailApp(String mailtoURL)
   {
      if (mailtoURL == null || !mailtoURL.startsWith("mailto:"))
      {
         throw new IllegalArgumentException("openInEmailApp() requires a mailto url");
      }
      launch(browserCmds(mailtoURL));
   }

   public static LinkedList<String> browserCmds(String url) { return cmds(url, false); }
   public static LinkedList<String> execCmds(String url) { return cmds(url, true); }

   public static LinkedList<String> cmds(String fileName, boolean exec)
   {
      LinkedList<String> cmds = new LinkedList<String>();
      
      if (Executable.isLinux())
      {
         cmds.add(String.format("gnome-open %s", fileName));
         String subCmd = (exec) ? "exec" : "openURL";
         cmds.add(String.format("kfmclient "+subCmd+" %s", fileName));
      }
      else if (Executable.isMac())
      {
         cmds.add(String.format("open %s", fileName));
      }
      else if (Executable.isWindows() && Executable.isWindows9X())
      {
         cmds.add(String.format("command.com /C start %s", fileName));
      }
      else if (Executable.isWindows())
      {
         cmds.add(String.format("cmd /c start %s", fileName));
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
         if (!cmds.isEmpty())
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
