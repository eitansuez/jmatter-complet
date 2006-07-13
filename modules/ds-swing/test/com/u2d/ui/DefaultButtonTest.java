package com.u2d.ui;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 4:15:11 PM
 */

import junit.framework.TestCase;

import javax.swing.*;

public class DefaultButtonTest
      extends TestCase
{


   public void testDefaultButton()
   {
      // create a frame
      // add a button
      // set frame visible
      // check that it is the default button
      JFrame f= new JFrame();
      DefaultButton defaultBtn = new DefaultButton("Hello");
      f.add(defaultBtn);
      f.setVisible(true);

      JRootPane rootPane = defaultBtn.getRootPane();
      assertEquals(defaultBtn, rootPane.getDefaultButton());
   }
}