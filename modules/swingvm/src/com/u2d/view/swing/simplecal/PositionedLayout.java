/*
 * Created on Sep 17, 2003
 */
package com.u2d.view.swing.simplecal;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.u2d.calendar.*;

/**
 * @author Eitan Suez
 */
public class PositionedLayout implements LayoutManager2
{
	private TimeIntervalView _view;
	private java.util.List _events = new ArrayList();
	private java.util.List _components = new ArrayList();
	
	public PositionedLayout(TimeIntervalView view)
	{
		_view = view;
	}

	public void addLayoutComponent(Component comp, Object constraints)
	{
		synchronized (comp.getTreeLock())
		{
			if (!(constraints instanceof CalEvent))
			{
				throw new IllegalArgumentException("cannot add to layout:  contstraing must be an event");
			}
         CalEvent event = (CalEvent) constraints;
			_events.add(event);
			_components.add(comp);
		}
	}
	
	public void removeLayoutComponent(Component comp)
	{
		synchronized (comp.getTreeLock())
		{
			for (int i=0; i<_components.size(); i++)
			{
				if (_components.get(i) == comp)
				{
					_components.remove(i);
					_events.remove(i);
				}
			}
		}
	}

	public float getLayoutAlignmentX(Container target)
	{
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container target)
	{
		return 0.5f;
	}

	public void invalidateLayout(Container target)
	{  // noop
	}

	public Dimension minimumLayoutSize(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			return ((JComponent) _view).getMinimumSize();
		}
	}

	public Dimension preferredLayoutSize(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			return ((JComponent) _view).getPreferredSize();
		}
	}

	public Dimension maximumLayoutSize(Container target)
	{
		synchronized (target.getTreeLock())
		{
			return ((JComponent) _view).getMaximumSize();
		}
	}

	public void addLayoutComponent(String name, Component comp)
	{
		throw new IllegalArgumentException("use addLayoutComponent(comp, constraints) instead");
	}

	public void layoutContainer(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
         JComponent viewcomp = (JComponent) _view;
			viewcomp.setBounds(parent.getBounds());

			for (int i=0; i<_components.size(); i++)
			{
            Component comp = (Component) _components.get(i);
            CalEvent event = (CalEvent) _events.get(i);
            comp.setBounds(_view.getBounds(event));
			}
		}
	}

}
