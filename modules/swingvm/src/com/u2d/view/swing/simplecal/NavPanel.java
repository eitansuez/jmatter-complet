/*
 * Created on Apr 13, 2004
 */
package com.u2d.view.swing.simplecal;

import javax.swing.*;
import com.u2d.ui.IconButton;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.view.swing.SwingAction;

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
   
   private TimeSheet _timesheet;

   public NavPanel(TimeSheet timesheet)
   {
      _timesheet = timesheet;
      
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
   
   public void actionPerformed(final ActionEvent evt)
   {
      SwingViewMechanism.invokeSwingAction(new SwingAction()
      {
         public void offEDT()
         {
            boolean forward = ">".equals(evt.getActionCommand());
            _timesheet.shift(forward);
         }
         public void backOnEDT() { }
      });

   }

}
