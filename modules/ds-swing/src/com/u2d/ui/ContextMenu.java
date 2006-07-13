/*
 * Created on Oct 7, 2003
 */
package com.u2d.ui;

import java.awt.event.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class ContextMenu extends JPopupMenu
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
		
		for (int i=0; i<menuitemlist.length; i++)
		{
			add(menuitemlist[i]);
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
      public void mousePressed(MouseEvent evt)
      {
         if (evt.isPopupTrigger() && isEnabled())
            show(evt.getComponent(), evt.getX(), evt.getY());
      }
      // for microsoft platform:
      public void mouseReleased(MouseEvent evt)
      {
         if (evt.isPopupTrigger() && isEnabled())
            show(evt.getComponent(), evt.getX(), evt.getY());
      }
   }
   
}
