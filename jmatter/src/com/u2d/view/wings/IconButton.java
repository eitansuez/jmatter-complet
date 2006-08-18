package com.u2d.view.wings;

import org.wings.SButton;
import org.wings.SImageIcon;
import org.wings.border.SEmptyBorder;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class IconButton extends SButton
{
   public IconButton(Icon icon, Icon rollover)
   {
      super(new SImageIcon((ImageIcon) icon));
      setBorder(new SEmptyBorder(1,2,1,2));
//      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//      setContentAreaFilled(false);
//      setRolloverEnabled(true);
      setRolloverIcon(new SImageIcon((ImageIcon) rollover));
   }
}
