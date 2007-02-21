package com.u2d.view.swing;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 21, 2007
 * Time: 4:47:33 PM
 */
public class KeyTest
{
   public static void main(String[] args)
   {
      JFrame f = new JFrame();
      f.getContentPane().add(btn());
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,400,400);
      f.setVisible(true);
   }
   private static JButton btn()
   {
      JButton btn = new JButton("stam");
      btn.addKeyListener(new KeyListener()
      {
         public void keyTyped(KeyEvent e)
         {
            printKeyEventInfo("keytyped", e);
         }

         public void keyPressed(KeyEvent e)
         {
            printKeyEventInfo("keypressed", e);
         }

         public void keyReleased(KeyEvent e)
         {
            printKeyEventInfo("keyrelease", e);
         }
      });
      return btn;
   }
   
   private static void printKeyEventInfo(String evtmsg, KeyEvent e)
   {
      System.out.println(evtmsg+":");
      System.out.println("\t keycode:  "+e.getKeyCode());
   }
}
