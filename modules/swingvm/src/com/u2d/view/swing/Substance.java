package com.u2d.view.swing;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SkinInfo;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 16, 2009
 * Time: 3:09:08 PM
 */
public class Substance
{
   public static void addSkinMenuTo(JMenuBar menubar)
   {
      if (!usingSubstance()) return;
      menubar.add(substanceSkinMenu());
   }
   public static void addSkinSubmenuTo(JMenu menu)
   {
      if (!usingSubstance()) return;
      menu.add(substanceSkinMenu());
   }

   private static JMenu substanceSkinMenu()
   {
      JMenu menu = new JMenu("Skin");
      menu.setMnemonic('k');
      fillMenuWithSkins(menu);
      return menu;
   }
   private static void fillMenuWithSkins(JMenu menu)
   {
      final Map<String, SkinInfo> skins = SubstanceLookAndFeel.getAllSkins();
      ActionListener a = new ActionListener()
      {
         public void actionPerformed(final ActionEvent e)
         {
            SwingUtilities.invokeLater(new Runnable() {
               public void run()
               {
                  SkinInfo skinInfo = skins.get(e.getActionCommand());
                  SubstanceLookAndFeel.setSkin(skinInfo.getClassName());
               }
            });
         }
      };
      for (Map.Entry<String, SkinInfo> skin : skins.entrySet())
      {
         JMenuItem menuItem = new JMenuItem(skin.getValue().getDisplayName());
         menuItem.setActionCommand(skin.getKey());
         menuItem.addActionListener(a);
         menu.add(menuItem);
      }
   }

   public static boolean usingSubstance()
   {
      return UIManager.getLookAndFeel() instanceof SubstanceLookAndFeel;
   }
}
