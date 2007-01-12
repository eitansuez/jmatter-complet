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
public class GridListLayout implements LayoutManager
{
	int _hgap, _vgap;
	int _defaultWidth, _defaultHeight;
   Grid _grid;

   
   public GridListLayout()
	{
		this(0, 0);
	}
	public GridListLayout(int hgap, int vgap)
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
	
   public Dimension getPreferredSize(Container parent)
   {
      return computeLayoutSize(parent, true);
   }
	
   public Dimension minimumLayoutSize(Container parent)
   {
      return preferredLayoutSize(parent);
   }

	/**
	 * @param parent container
	 * @param proportional whether to consider parent's width as a constraint when computing size
    * @return layout size
	 */
	private Dimension computeLayoutSize(Container parent, boolean proportional)
	{
		int count = parent.getComponentCount();
		if (count == 0)
			return parent.getSize();
		
		_grid = calculateSizes(parent, proportional);

      Insets insets = parent.getInsets();
		return new Dimension( _grid.width() + insets.left + insets.right,
                            _grid.height() + insets.top + insets.bottom );
	}
	
	public void layoutContainer(Container parent)
	{
		int count = parent.getComponentCount();
		if (count == 0) return;
		
		_grid = deriveSizesFromParent(parent);
		
		//System.out.println(nrows + " x " + ncols);

		Insets insets = parent.getInsets();
		
		int index, x, y;
		Component comp = null;
		for (int row=0; row<_grid.nrows; row++)
		{
			for (int col=0; col<_grid.ncols; col++)
			{
				index = row*_grid.ncols + col;
				if (index >= count) break;
				comp = parent.getComponent(index);
				
				x = insets.left + col * (_grid.compWidth + _hgap);
				y = insets.top + row * (_grid.compHeight + _vgap);
				comp.setBounds(x, y, _grid.compWidth, _grid.compHeight);
			}
		}
	}
   
   private Grid deriveSizesFromParent(Container parent)
	{
		return calculateSizes(parent, false);
	}
	private Grid calculateSizes(Container parent, boolean proportional)
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
		
      return new Grid(ncols, nrows, compSize.width, compSize.height);
	}
   
   public int locationToIndex(Point location)
   {
      if (_grid == null) return -1;
      return _grid.locationToIndex(location);
   }
   
   public Point indexToLocation(int index)
   {
      return _grid.indexToLocation(index);
   }
   
   public int nextRowIndex(int index, int size, int amount)
   {
      return _grid.nextRowIndex(index, size, amount);
   }

   public Rectangle getCellBounds(int index)
   {
      Point location = indexToLocation(index);
      return new Rectangle(location, new Dimension(_grid.compWidth, _grid.compHeight));
   }
   
   
   class Grid
   {
      int nrows, ncols;
      int compWidth, compHeight;
      
      public Grid(int cols, int rows, int width, int height)
      {
         ncols = cols; nrows = rows; compWidth = width; compHeight = height;
      }
      
      public int width()
      {
         return ncols * (compWidth + _hgap);
      }
      public int height()
      {
         return nrows * (compHeight + _vgap);
      }
      
      public int locationToIndex(Point location)
      {
         int rowindex = location.y / ( compHeight + _vgap );
         int colindex = location.x / ( compWidth + _hgap );
         return (rowindex * ncols) + colindex;
      }
      public Point indexToLocation(int index)
      {
         int rowindex = index / ncols;
         int colindex = index % ncols;
         return new Point(colindex * ( compWidth + _hgap ), rowindex * ( compHeight + _vgap ));
      }
      public int nextRowIndex(int index, int size, int amt)
      {
         int rowindex = index / ncols;
         int colindex = index % ncols;
         rowindex+=amt;
         if (rowindex < 0)
         {
            rowindex += nrows;  // because modulo operator doesn't work with negative numbers
            // e.g. for -1 % 3 i want to get back 2 but instead i get -1
         }
         rowindex%=nrows;
         int next = (rowindex * ncols) + colindex;
         if (next >= size)
         {
            rowindex+=amt;
            rowindex%=nrows;
            next = (rowindex * ncols) + colindex;
         }
         return next;
      }
   }
   
	
   /* ----- */
	
	public static void main(String[] args)
	{
		JFrame f = new JFrame("Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pnl = new JPanel(new GridListLayout());

		ClassLoader loader = GridListLayout.class.getClassLoader();
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
