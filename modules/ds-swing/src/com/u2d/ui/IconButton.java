/*
 * Created on May 5, 2005
 */
package com.u2d.ui;

import java.awt.Cursor;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Eitan Suez
 */
public class IconButton extends JButton
{
   public IconButton(Icon icon)
   {
      super(icon);
      setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setContentAreaFilled(false);
   }
   public IconButton(Icon icon, Icon rollover)
   {
      this(icon);
      setRolloverEnabled(true);
      setRolloverIcon(rollover);
   }
}
