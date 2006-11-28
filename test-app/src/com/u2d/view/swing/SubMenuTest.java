/*
 * Created on Dec 14, 2004
 */
package com.u2d.view.swing;

import javax.swing.*;

/**
 * The reason for this test is to find out whether multiple
 * mutually exclusive items belonging to the same group can
 * actually be spread out across more than one menu or submenu.
 * 
 * the answer is yes!
 * 
 * @author Eitan Suez
 */
public class SubMenuTest
{
   public static void main(String[] args)
   {
      new SubMenuTest();
   }
   
   public SubMenuTest()
   {
      JFrame f = new JFrame("SubMenu Test");
      
      JMenuBar mb = new JMenuBar();
      JMenu testMenu = new JMenu("Test");
      mb.add(testMenu);
      JMenu sub1 = new JMenu("test1");
      JMenu sub2 = new JMenu("test2");
      testMenu.add(sub1);
      testMenu.add(sub2);
      
      JRadioButtonMenuItem mi11 = new JRadioButtonMenuItem("test 11");
      JRadioButtonMenuItem mi12 = new JRadioButtonMenuItem("test 12");
      JRadioButtonMenuItem mi21 = new JRadioButtonMenuItem("test 21");
      JRadioButtonMenuItem mi22 = new JRadioButtonMenuItem("test 22");
      
      ButtonGroup bg = new ButtonGroup();
      bg.add(mi11);
      bg.add(mi12);
      bg.add(mi21);
      bg.add(mi22);
      
      sub1.add(mi11);
      sub1.add(mi12);
      sub2.add(mi21);
      sub2.add(mi22);
      
      f.setJMenuBar(mb);
      
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }

}
