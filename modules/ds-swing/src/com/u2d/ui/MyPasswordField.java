package com.u2d.ui;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 1:41:59 PM
 *
 * @author Eitan Suez
 */
public class MyPasswordField extends JPasswordField
{
   public MyPasswordField()
   {
      super();
      setup();
   }
   public MyPasswordField(int columns)
   {
      super(columns);
      setup();
   }

   private void setup() { UIUtils.selectOnFocus(this); }
}
