package com.u2d.ui;

import com.u2d.ui.desktop.EnhDesktopPane;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 6, 2005
 * Time: 9:09:07 AM
 */
public class MsgDlgTest
{
   EnhDesktopPane dp;
   
   public MsgDlgTest()
   {
      JFrame f = new JFrame();
      JPanel p = (JPanel) f.getContentPane();

      dp = new EnhDesktopPane();
      
      p.add(btn(), BorderLayout.NORTH);
      p.add(dp, BorderLayout.CENTER);

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,500,500);
      f.setVisible(true);
   }
   private JButton btn()
   {
      JButton btn = new JButton("Show Dlg");
      btn.setMnemonic('s');
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            new Thread()
            {
               public void run()
               {
                  for (int i=1; i<100; i++)
                  {
                     final String msg = "" + i;
                     SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           dp.message(msg);
                        }
                     });

                     try
                     {
                        Thread.sleep(150);
                     }
                     catch (InterruptedException e1)
                     {
                        e1.printStackTrace();
                     }
                  }
               }
            }.start();
         }
      });
         
      return btn;
   }
   public static void main(String[] args)
   {
      System.setProperty("swing.aatext", "true");
      new MsgDlgTest();
   }
}
