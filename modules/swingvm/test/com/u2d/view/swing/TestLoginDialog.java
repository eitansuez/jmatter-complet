package com.u2d.view.swing;

import com.u2d.app.AuthManager;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Mar 30, 2009
 * Time: 12:17:28 PM
 */
public class TestLoginDialog extends JPanel implements AuthManager
{
   public TestLoginDialog()
   {
      setLayout(new BorderLayout());
      add(new LoginDialog(this), BorderLayout.CENTER);
   }

   public void onLogin(String username, String password)
   {
      System.out.printf("User %s logged in\n", username);
   }

   public static void main(String[] args)
   {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setContentPane(new TestLoginDialog());
      f.setLocationRelativeTo(null);
      f.pack();
      f.setVisible(true);
   }
}
