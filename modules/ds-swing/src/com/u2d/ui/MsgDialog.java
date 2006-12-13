/*
 * Created on Dec 19, 2003
 */
package com.u2d.ui;

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Eitan Suez
 */
public class MsgDialog extends JWindow
{

   MsgDialog(Container parent, String msg, String title)
   {
      JXPanel content = new JXPanel(new BorderLayout());
      content.setAlpha(0.8f);
      setContentPane(content);

      JPanel msgPnl = new JPanel();
      msgPnl.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));
      JLabel msgLabel = new JLabel(msg);
      msgPnl.add(msgLabel);
      content.add(msgPnl, BorderLayout.CENTER);

      content.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            MsgDialog.this.dispose();
         }
      });
      content.addKeyListener(new KeyAdapter()
      {
         public void keyPressed(KeyEvent evt)
         {
            MsgDialog.this.dispose();
         }
      });

      setBackground(Color.white);
      content.setBorder(BorderFactory.createLineBorder(Color.black));

      pack();
      Point p = UIUtils.computeCenter(parent, this);
      while (!parent.isShowing())
         parent = parent.getParent();
      setLocation(parent.getLocationOnScreen().x + p.x , parent.getLocationOnScreen().y + p.y);

      startTimer();
   }

   private void startTimer()
   {
      new Thread()
      {
         public void run()
         {
            try {
               Thread.sleep(2000);
            } catch (InterruptedException ex) {}
            MsgDialog.this.dispose();
         }
      }.start();
   }

   public static void showMsgDlg(Container parent, String msg, String title)
   {
      MsgDialog msgDialog = new MsgDialog(parent, msg, title);
      msgDialog.setVisible(true);
   }

}
