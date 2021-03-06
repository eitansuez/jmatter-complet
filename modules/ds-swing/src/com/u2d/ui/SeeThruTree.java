/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Eitan Suez
 */
public class SeeThruTree extends JTree
{
	public SeeThruTree(TreeNode node)
	{
		super(node);
		init();
	}
	public SeeThruTree()
	{
		init();
	}
	
	private void init()
	{
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
					dispatch(evt);
			}
			// for microsoft platform:
			public void mouseReleased(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
					dispatch(evt);
			}
			public void mouseClicked(MouseEvent evt)
			{
				dispatch(evt);
			}
			private void dispatch(MouseEvent evt)
			{
				TreePath path = getPathForLocation(evt.getX(), evt.getY());
				if (path == null) return;
				
				Object eo = path.getLastPathComponent();
				if (eo == null)
					return;
				
				setSelectionPath(path);  // is this necessary?

				TreeCellRenderer renderer = getCellRenderer();
            
				int row = getRowForLocation(evt.getX(), evt.getY());
				Component item = renderer.getTreeCellRendererComponent(SeeThruTree.this, eo, true,
						false, getModel().isLeaf(eo), row, true);
				item.dispatchEvent(evt);
			}
		});
	}

}
