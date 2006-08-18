package com.u2d.view.wings;

import java.awt.event.ActionListener;
import org.wings.SToggleButton;
import org.wings.SImageIcon;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Eitan Suez
 */
class LockToggle extends SToggleButton
{
   private static Icon ICON_LOCKED, ICON_UNLOCKED, ICON_LOCKED_ROLLOVER,
      ICON_UNLOCKED_ROLLOVER;
   static
   {
      ClassLoader loader = com.u2d.ui.LockedButton.class.getClassLoader();
      java.net.URL imgurl = loader.getResource("images/locked-16.png");
      ICON_LOCKED = new ImageIcon(imgurl);
      imgurl = loader.getResource("images/unlocked-16.png");
      ICON_UNLOCKED = new ImageIcon(imgurl);
      imgurl = loader.getResource("images/locked-16_rollover.png");
      ICON_LOCKED_ROLLOVER = new ImageIcon(imgurl);
      imgurl = loader.getResource("images/unlocked-16_rollover.png");
      ICON_UNLOCKED_ROLLOVER = new ImageIcon(imgurl);
   }

   public LockToggle(ActionListener listener)
   {
      super(new SImageIcon((ImageIcon) ICON_LOCKED));
      setSelectedIcon(new SImageIcon((ImageIcon) ICON_UNLOCKED));
      addActionListener(listener);
//      setBorderPainted(false);
//      setMargin(new Insets(0,0,0,0));
//      setContentAreaFilled(false);
//      setFocusPainted(true);
//      setRolloverEnabled(true);
      setRolloverIcon(new SImageIcon((ImageIcon) ICON_LOCKED_ROLLOVER));
      setRolloverSelectedIcon(new SImageIcon((ImageIcon) ICON_UNLOCKED_ROLLOVER));
   }

   public boolean isLocked() { return !isSelected(); }
}

