/*
 * Created on Oct 30, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class DefaultScrollable implements Scrollable
{
	JComponent _comp;
	
	public DefaultScrollable(JComponent c)
	{
		_comp = c;
	}

	public boolean getScrollableTracksViewportHeight()
	{
		if (_comp.getParent() instanceof JViewport)
		{
			JViewport viewport = (JViewport) _comp.getParent();
			return (viewport.getHeight() > _comp.getPreferredSize().height);
		}
		return false;
	}

	public boolean getScrollableTracksViewportWidth()
	{
		if (_comp.getParent() instanceof JViewport)
		{
			JViewport viewport = (JViewport) _comp.getParent();
			return (viewport.getHeight() > _comp.getPreferredSize().height);
		}
		return false;
	}

	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction)
	{
		return (orientation == SwingConstants.HORIZONTAL) ? 80 : 30;
	}

	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction)
	{
		return (orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : visibleRect.height;
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		if (_comp.getParent() instanceof JViewport)
		{
//			JViewport viewport = (JViewport) _comp.getParent();  // i think need to somehow account for scrollbar width
			// for horizontal and scrollbar height for vertical or maybe for insets of container
			return new Dimension(_comp.getPreferredSize().width + 15, _comp.getPreferredSize().height + 15);
		}
		return _comp.getPreferredSize();
	}
	
}
