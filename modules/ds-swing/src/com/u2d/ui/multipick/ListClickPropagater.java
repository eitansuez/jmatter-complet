/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui.multipick;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class ListClickPropagater extends JList
{

	public ListClickPropagater()
	{
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
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
		Component item = renderer.getListCellRendererComponent(ListClickPropagater.this, value, index, false, false);

		item.dispatchEvent(evt);
	}

}
