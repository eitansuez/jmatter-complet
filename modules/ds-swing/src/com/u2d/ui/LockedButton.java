/*
 * Created on Nov 2, 2004
 */
package com.u2d.ui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class LockedButton extends JButton implements ActionListener
{
   private LockToggle _lock;

   public LockedButton(String caption)
   {
      super(caption);
      initialize();
   }
   
   public LockedButton(Action action)
   {
      super(action);
      initialize();
   }
   
   private void initialize()
   {
      setHorizontalAlignment(SwingConstants.CENTER);
      _lock = new LockToggle(this);
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      Dimension min = new Dimension(0,0);
      Dimension preferred = getPreferredSize();
      Dimension max = new Dimension(500, 3);
      Box.Filler elastic = new Box.Filler(min, preferred, max);
      add(elastic);
      add(_lock);
      setOpaque(false);
      super.setEnabled(false);
   }
   
   public void setEnabled(boolean enabled)
   {
      if (_lock == null)
      {
         super.setEnabled(enabled);
         return;
      }
      _lock.setEnabled(enabled);
      super.setEnabled(!_lock.isLocked() && enabled);
   }
   
   public void actionPerformed(ActionEvent evt)
   {
      super.setEnabled(!_lock.isLocked());
   }
   
   public Insets getInsets()
   {
      return new Insets(2,8,2,2);
   }
   
}
