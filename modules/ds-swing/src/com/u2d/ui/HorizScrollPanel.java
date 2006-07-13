/*
 * Created on Oct 29, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class HorizScrollPanel extends JPanel implements Scrollable
{
	private int _scrollbarHeight = 15;
	
	public HorizScrollPanel()
	{
		setLayout(new BorderLayout());
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		// why do _i_ need to compensate for the freaking scrollbar??
		if (getParent() instanceof JViewport && isScrollbarOn())
		{
//			JViewport viewport = (JViewport) getParent();  // i think need to somehow account for scrollbar width
			// for horizontal and scrollbar height for vertical or maybe for insets of container
			return new Dimension(getPreferredSize().width, getPreferredSize().height + _scrollbarHeight);
		}
		return getPreferredSize();
	}
	
	private boolean isScrollbarOn()
	{
		return !getScrollableTracksViewportWidth();
	}
	
	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction)
	{
		return visibleRect.width - 50;
	}
	
	public boolean getScrollableTracksViewportHeight()
	{
		return true;
	}

	public boolean getScrollableTracksViewportWidth()
	{
		if (getParent() instanceof JViewport)
		{
			JViewport viewport = (JViewport) getParent();
			return (viewport.getWidth() > getPreferredSize().width);
		}
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 50;
	}
   
   int _maxHeight = 100;
   
   public Dimension getPreferredSize()
   {
      Dimension dim = super.getPreferredSize();
      dim.height = Math.min(dim.height, _maxHeight);
      return dim;
   }


}
