package com.u2d.view.wings;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.wings.SButton;
import org.wings.SConstants;
import org.wings.SBoxLayout;

/**
 * @author Eitan Suez
 */
public class LockedButton extends SButton implements ActionListener
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
      setHorizontalAlignment(SConstants.LEFT);
      _lock = new LockToggle(this);
//      add(_lock);
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

}
