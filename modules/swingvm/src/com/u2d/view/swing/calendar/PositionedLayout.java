/*
 * Created on Sep 17, 2003
 */
package com.u2d.view.swing.calendar;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.u2d.calendar.*;

/**
 * @author Eitan Suez
 */
public class PositionedLayout implements LayoutManager2
{
	private TimeIntervalView _view;
	private java.util.List<CalEvent> _events = new ArrayList<CalEvent>();
	private java.util.Map<CalEvent, Component> _components = new HashMap<CalEvent, Component>();

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
			_components.put(event, comp);
		}
	}
	
	public void removeLayoutComponent(Component comp)
	{
		synchronized (comp.getTreeLock())
		{
         Set entrySet = _components.entrySet();
         for (Iterator itr = entrySet.iterator(); itr.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) itr.next();
            Component c = (Component) entry.getValue();
            if (c == comp)
            {
               CalEvent event = (CalEvent) entry.getKey();
               _events.remove(event);
               _components.remove(entry);
               return;
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

   private List<CalEvent> calcOverlappingEventsFor(CalEvent targetEvent)
   {
      List<CalEvent> overlappingEvents = new ArrayList<CalEvent>();
      for (CalEvent evt : _events)
      {
         if ((evt == targetEvent) || (evt.timeSpan().overlapsWith(targetEvent.timeSpan())))
         {
            overlappingEvents.add(evt);
         }
      }
      return overlappingEvents;
   }

   static int event_padding = 4;

	public void layoutContainer(Container parent)
	{
      synchronized (parent.getTreeLock())
		{
         JComponent viewcomp = (JComponent) _view;
			viewcomp.setBounds(parent.getBounds());

         Collections.sort(_events);
         int xPos = 0;

         CalEvent prevEvt = null;
         for (CalEvent calEvent : _events)
         {
            List<CalEvent> overlappingEvents = calcOverlappingEventsFor(calEvent);

            Rectangle bounds = _view.getBounds(calEvent);
            int numOverlapping = overlappingEvents.size();
            int width = bounds.width / numOverlapping;
            if (xPos + width > bounds.width)
            {
               // first, correct x position of previous event (align right to avoid overlap conflict
               // with next overlapping items)
               if (prevEvt != null && overlappingEvents.contains(prevEvt))
               {
                  Component prevComp = _components.get(prevEvt);
                  Point location = prevComp.getLocation();
                  int x = bounds.x + bounds.width - (prevComp.getWidth() + event_padding/2);
                  prevComp.setLocation(x, location.y);
               }

               // second, carriage-return logic
               xPos = 0;
               for (CalEvent e : overlappingEvents)
               {
                  if (e == calEvent) break;
                  Rectangle ebounds = _components.get(e).getBounds();
                  int pos = xPos + (event_padding / 2);
                  if (ebounds.getX() - bounds.x == pos)
                  {
                     xPos += _components.get(e).getWidth() + event_padding;
                  }
               }
            }

            Rectangle eventBounds = new Rectangle(bounds.x + xPos + event_padding/2, bounds.y, width - event_padding, bounds.height);
            _components.get(calEvent).setBounds(eventBounds);
            
            xPos += xPos + width;

            prevEvt = calEvent;
         }


      }
   }

}
