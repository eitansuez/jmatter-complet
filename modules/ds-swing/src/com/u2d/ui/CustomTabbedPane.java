package com.u2d.ui;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 22, 2006
 * Time: 4:40:46 PM
 */
public class CustomTabbedPane extends JTabbedPane
{
   public CustomTabbedPane()
   {
      setTabLayoutPolicy(WRAP_TAB_LAYOUT);
      setTabPlacement(TOP);
   }

   public void addNotify()
   {
      super.addNotify();
      setSelectedIndex(0);
      setupKeyBindings();
   }

   private void setupKeyBindings()
   {
      for (int i=1; i<=getTabCount(); i++)
      {
         KeyStroke shortcut = KeyStroke.getKeyStroke(
                                   Character.forDigit(i, 10),
                                   InputEvent.ALT_MASK);
         String mapKey = "FOCUS_TAB_"+i;
         final int index = i - 1;
         getInputMap(WHEN_IN_FOCUSED_WINDOW).put(shortcut, mapKey);
         getActionMap().put(mapKey, new AbstractAction()
         {
            public void actionPerformed(ActionEvent evt)
            {
               setSelectedIndex(index);
            }
         });
      }
   }
}
