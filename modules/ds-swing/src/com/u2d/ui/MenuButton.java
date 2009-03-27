package com.u2d.ui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 19, 2005
 * Time: 12:15:31 PM
 */
public class MenuButton extends JButton
{
   JPopupMenu _menu;

   public MenuButton(JPopupMenu menu)
   {
      super();
      _menu = menu;

      addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _menu.show(MenuButton.this, 0, MenuButton.this.getHeight());
         }
      });
   }
   public MenuButton(String caption, JPopupMenu menu)
   {
      this(menu);
      setText(caption);
   }

   public MenuButton(Icon icon, Icon rollover, JPopupMenu menu)
   {
      this(menu);

      setIcon(icon);

      setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setContentAreaFilled(false);

      setFocusPainted(false);
      setFocusable(false);

      setRolloverEnabled(true);
      setRolloverIcon(rollover);
   }

   public JPopupMenu menu() { return _menu; }
}
