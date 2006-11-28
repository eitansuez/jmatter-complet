package com.u2d.view.swing;

import com.u2d.app.AuthManager;
import javax.swing.*;
import java.awt.*;

/**
 * Date: May 26, 2005
 * Time: 8:13:28 AM
 *
 * @author Eitan Suez
 */
public class LoginTest
{
   public LoginTest()
   {
      JFrame f = new JFrame();
      JPanel p = (JPanel) f.getContentPane();
      JDesktopPane dp = new JDesktopPane();
      p.add(dp, BorderLayout.CENTER);

      AuthManager mgr = new AuthManager()
      {
         public void onLogin(String username, String password)
         {
            System.out.println("username: "+username+"; password: "+password);
         }
      };


      f.setBounds(100,100,500,400);

      LoginDialog dlg = new LoginDialog(mgr);
      f.setVisible(true);

      dlg.clear();
      dlg.makeVisible();

   }


   public static void main(String[] args)
   {
//      try {
//          UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
//      } catch (Exception e) {
//         System.err.println("exception: "+e.getMessage());
//      }
      new LoginTest();
   }
}
