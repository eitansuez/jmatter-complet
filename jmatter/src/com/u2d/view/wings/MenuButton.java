package com.u2d.view.wings;

import org.wings.*;
import org.wings.border.SEmptyBorder;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 19, 2005
 * Time: 12:15:31 PM
 */
public class MenuButton extends SButton
{
   SPopupMenu _menu;

   public MenuButton(SPopupMenu menu)
   {
      super();
      _menu = menu;
      setComponentPopupMenu(_menu);

//      addActionListener(new ActionListener()
//      {
//         public void actionPerformed(ActionEvent e)
//         {
//            _menu.show(MenuButton.this, 0, MenuButton.this.getHeight());
//         }
//      });
   }
   public MenuButton(String caption, SPopupMenu menu)
   {
      this(menu);
      setText(caption);
   }

   public MenuButton(Icon icon, Icon rollover, SPopupMenu menu)
   {
      this(menu);
      setIcon(new SImageIcon((ImageIcon) icon));
      setBorder(new SEmptyBorder(1,2,1,2));
      setRolloverIcon(new SImageIcon((ImageIcon) rollover));
   }
}
