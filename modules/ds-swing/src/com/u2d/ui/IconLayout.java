/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * Layout manager with behaviour similar to what a filemanager would do for icons.
 * That is, icons are expected to all be the same size, are laid out along rows
 * and columns, row major.  if not enough room, always scroll vertical (that is,
 * width dictated by container.
 *
 * Designed to be used with IconPanel which is Scrollable
 * 
 * @author Eitan Suez
 */
public class IconLayout implements LayoutManager
{
	int _hgap, _vgap;
	int _defaultWidth, _defaultHeight;
	
	public IconLayout()
	{
		this(0, 0);
	}
	public IconLayout(int hgap, int vgap)
	{
		_hgap = hgap;
		_vgap = vgap;
		_defaultWidth = _defaultHeight = 30;
	}
	
	public int getHgap() { return _hgap; }
	public int getVgap() { return _vgap; }
	public int getDefaultWidth() { return _defaultWidth; }
	public int getDefaultHeight() { return _defaultHeight; }

	public void addLayoutComponent(String name, Component comp) {}
	public void removeLayoutComponent(Component comp) {}

	public Dimension preferredLayoutSize(Container parent)
	{
		return computeLayoutSize(parent, false);
	}
	
	/**
	 * @param parent container
	 * @param proportional whether to consider parent's width as a constraint when computing size
	 */
	private Dimension computeLayoutSize(Container parent, boolean proportional)
	{
		int count = parent.getComponentCount();
		if (count == 0)
			return parent.getSize();
		
		int[] sizes = calculateSizes(parent, proportional);
		int ncols = sizes[0];  int nrows = sizes[1];
		int compWidth = sizes[2];  int compHeight = sizes[3];

		int width = ncols * (compWidth + _hgap);
		int height = nrows * (compHeight + _vgap);
		Insets insets = parent.getInsets();
		return new Dimension( width + insets.left + insets.right,
				height + insets.top + insets.bottom );
	}
	
	public Dimension getPreferredSize(Container parent)
	{
		return computeLayoutSize(parent, true);
	}
	
	public Dimension minimumLayoutSize(Container parent)
	{
		return preferredLayoutSize(parent);
	}

	public void layoutContainer(Container parent)
	{
		int count = parent.getComponentCount();
		if (count == 0) return;
		
		int[] sizes = deriveSizesFromParent(parent);
		int ncols = sizes[0];  int nrows = sizes[1];
		int compWidth = sizes[2];  int compHeight = sizes[3];
		
		//System.out.println(nrows + " x " + ncols);

		Insets insets = parent.getInsets();
		
		int index, x, y;
		Component comp = null;
		for (int row=0; row<nrows; row++)
		{
			for (int col=0; col<ncols; col++)
			{
				index = row*ncols + col;
				if (index >= count) break;
				comp = parent.getComponent(index);
				
				x = insets.left + col * (compWidth + _hgap);
				y = insets.top + row * (compHeight + _vgap);
				comp.setBounds(x, y, compWidth, compHeight);
			}
		}
	}
	
	
	private int[] deriveSizesFromParent(Container parent)
	{
		return calculateSizes(parent, false);
	}
	private int[] calculateSizes(Container parent, boolean proportional)
	{
		Component comp = parent.getComponent(0);
		Dimension compSize = comp.getPreferredSize();
		if (compSize.width == 0) compSize.width = _defaultWidth;
		if (compSize.height == 0) compSize.height = _defaultHeight;
		
		int count = parent.getComponentCount();
		
		for (int i=0; i<count; i++)
		{
         compSize.width = Math.max(compSize.width, parent.getComponent(i).getPreferredSize().width);
         compSize.height = Math.max(compSize.height, parent.getComponent(i).getPreferredSize().height);
		}
		
		int ncols = parent.getWidth() / ( compSize.width + _hgap );
		if (proportional)
			ncols = (int) Math.ceil(Math.sqrt((double) count));
		ncols = Math.max(ncols, 1);
							 
		int nrows = (count / ncols );
		nrows += (count % ncols == 0) ? 0 : 1; 
		nrows = Math.max(nrows, 1);
		
		ncols = Math.min(count, ncols);
		
		return new int[] {ncols, nrows, compSize.width, compSize.height};
	}
	
	
	
	/* ----- */
	
	public static void main(String[] args)
	{
		JFrame f = new JFrame("Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pnl = new JPanel(new IconLayout());

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		java.net.URL url = loader.getResource("images/Clinic32.gif");
		ImageIcon icon = new ImageIcon(url);

		int n = 39;
		for (int i = 0; i < n; i++)
		{
			JLabel lbl = new JLabel(icon);
			pnl.add(lbl);
		}

		f.getContentPane().add(pnl);

		f.setBounds(300,100,400,400);
		f.setVisible(true);
	}
}
