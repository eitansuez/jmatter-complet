/*
 * Created on Oct 10, 2003
 */
package com.u2d.ui;

import java.awt.*;

import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class IconPanel extends JPanel implements Scrollable
{
	IconLayout _layout;
	
	public IconPanel()
	{
		_layout = new IconLayout();
		setLayout(_layout);
		setOpaque(false);
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		int count = getComponentCount();
		if (count == 0)
			return _layout.getDefaultHeight();
			
		// never scrolls horizontally, so don't need to inspect orientation
		// q: why direction?
		Component comp = getComponent(0);
		return comp.getHeight() + _layout.getVgap();
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		int rowHeight = getScrollableUnitIncrement(visibleRect, orientation, direction);
		int numRows = visibleRect.height / rowHeight;
		return  rowHeight * numRows;
	}
	
	public boolean getScrollableTracksViewportHeight()
	{
		if (getParent() instanceof JViewport)
		{
			JViewport viewport = (JViewport) getParent();
			return (viewport.getHeight() > getPreferredSize().height);
		}
		return false;
	}
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}
	
	public Dimension getPreferredScrollableViewportSize()
	{
      return _layout.getPreferredSize(this);
	}
	
	public Dimension getPreferredSize()
	{
		return _layout.preferredLayoutSize(this);
	}
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}
	public Dimension getMinimumSize()
	{
		Component comp = getComponent(0);
		if (comp == null)
			return new Dimension(32, 32);
		return comp.getSize();
	}
	
	
	// test:
	public static void main(String[] args)
	{
		JFrame f = new JFrame("Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		IconPanel pnl = new IconPanel();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		java.net.URL url = loader.getResource("images/Clinic32.gif");
		ImageIcon icon = new ImageIcon(url);

		int n = 7;
		for (int i = 0; i < n; i++)
		{
			JLabel lbl = new JLabel(icon);
			pnl.add(lbl);
		}

		f.getContentPane().add(new JScrollPane(pnl));

		f.setBounds(300,100,400,400);
		f.setVisible(true);
	}
}
