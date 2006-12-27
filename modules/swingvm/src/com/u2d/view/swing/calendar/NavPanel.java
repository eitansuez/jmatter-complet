/*
 * Created on Apr 13, 2004
 */
package com.u2d.view.swing.calendar;

import javax.swing.*;
import com.u2d.ui.IconButton;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Eitan Suez
 */
public class NavPanel extends JPanel implements ActionListener
{
   public static ImageIcon NEXT_ICON, PREV_ICON, NEXT_ROLLOVER, PREV_ROLLOVER;
   static
   {
      ClassLoader loader = NavPanel.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/next.png");
      NEXT_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/next_rollover.png");
      NEXT_ROLLOVER = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/previous.png");
      PREV_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/previous_rollover.png");
      PREV_ROLLOVER = new ImageIcon(imgURL);
   }
   
   private ICalView _calView;

   public NavPanel(ICalView calView)
   {
      _calView = calView;
      
      setLayout(new FlowLayout(FlowLayout.CENTER));
      setupButton(PREV_ICON, PREV_ROLLOVER, "<");
      setupButton(NEXT_ICON, NEXT_ROLLOVER, ">");
   }
   
   private JButton setupButton(ImageIcon icon,
         ImageIcon rolloverIcon, String actionCommand)
   {
      JButton btn = new IconButton(icon, rolloverIcon);
      btn.setActionCommand(actionCommand);
      btn.addActionListener(this);
      add(btn);
      return btn;
   }
   
   public void actionPerformed(ActionEvent evt)
   {
      boolean forward = ">".equals(evt.getActionCommand());
      _calView.shift(forward);
   }

}
