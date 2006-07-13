package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 6, 2005
 * Time: 9:09:07 AM
 */
public class MsgDlgTest
{
   public MsgDlgTest()
   {
      JFrame f = new JFrame();
      JPanel p = (JPanel) f.getContentPane();

      final JDesktopPane dp = new JDesktopPane();
      dp.setBackground(new Color(0x008000));
      
      p.add(dp, BorderLayout.CENTER);
      

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,500,500);
      f.setVisible(true);
      
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            MsgDialog.showMsgDlg(dp, "Hello World", "Hi");
         }
      });
   }
   public static void main(String[] args)
   {
      new MsgDlgTest();
   }
}
