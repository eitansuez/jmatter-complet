/*
 * Created on Oct 7, 2003
 */
package com.u2d.ui;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class ContextMenu extends BasePopupMenu
{
   private MouseListener _listener = new ContextMouseListener();
   protected JComponent _target;

	public ContextMenu(JComponent target)
	{
		super();
      _target = target;
      attach();
	}

	public ContextMenu(JMenuItem[] menuitemlist, JComponent target)
	{
		this(target);

      for (JMenuItem menuItem : menuitemlist)
      {
         add(menuItem);
      }
	}
   
   public void attach()
   {
      _target.addMouseListener(_listener);
   }
   public void detach()
   {
      _target.removeMouseListener(_listener);
   }
   
   
   class ContextMouseListener extends MouseAdapter
   {
      // for non-microsoft platforms:
      public void mousePressed(MouseEvent evt) { showIt(evt); }

      // for microsoft platform:
      public void mouseReleased(MouseEvent evt) { showIt(evt); }

      private void showIt(MouseEvent evt)
      {
         if (evt.isPopupTrigger() && isEnabled())
         {
            Component component = evt.getComponent();
            if (component.getComponentOrientation().isLeftToRight())
            {
               show(evt.getComponent(), evt.getX(), evt.getY());
            }
            else
            {
               show(evt.getComponent(), evt.getX()-getWidth(), evt.getY());
            }
         }
      }

   }

}
