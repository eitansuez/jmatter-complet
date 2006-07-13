/*
 * Created on Oct 28, 2003
 */
package com.u2d.ui;

import java.awt.event.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class DragAdapter extends MouseMotionAdapter
{
	private int _type;
	
	public DragAdapter(int type)
	{
		_type = type;
	}

	public void mouseDragged(MouseEvent evt)
	{
		if (SwingUtilities.isLeftMouseButton(evt))
		{	
			JComponent c = (JComponent) evt.getSource();
			TransferHandler th = c.getTransferHandler();
			th.exportAsDrag(c, evt, _type);
		}
	}
	
}
