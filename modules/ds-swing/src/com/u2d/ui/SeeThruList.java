/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class SeeThruList extends JList
{

	public SeeThruList()
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
		});
		
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent evt)
			{
				dispatch(evt);
			}
		});
	}

	private void dispatch(MouseEvent evt)
	{
		int index = locationToIndex(new Point(evt.getX(), evt.getY()));
		if (index < 0) return;

//		setSelectedIndex(index);

		Object value = getModel().getElementAt(index);
		ListCellRenderer renderer = getCellRenderer();
		Component item = renderer.getListCellRendererComponent(SeeThruList.this, value, index, false, false);

		item.dispatchEvent(evt);
	}
}
