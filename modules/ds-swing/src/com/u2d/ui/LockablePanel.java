package com.u2d.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 9, 2008
 * Time: 2:48:42 PM
 */
public class LockablePanel extends JPanel
{
   private Lockable _lockable;
   private LockToggle lock;
   
   public LockablePanel(Lockable lockable)
   {
      _lockable = lockable;
      lock = new LockToggle(new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            _lockable.setLocked(lock.isLocked());
         }
      });
      lock.setToolTipText(lockable.lockTooltip());
      // initial state sync..
      _lockable.setLocked(lock.isLocked());

      setLayout(new BorderLayout());
      JPanel lockDecor = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      lockDecor.add(lock);
      add(lockDecor, BorderLayout.SOUTH);
      add((JComponent) lockable, BorderLayout.CENTER);  // consider wrapping lockable in a jscrollpane..
   }
}
