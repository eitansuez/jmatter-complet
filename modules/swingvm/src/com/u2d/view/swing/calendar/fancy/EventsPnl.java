/*
 * Created on Apr 12, 2004
 */
package com.u2d.view.swing.calendar.fancy;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import com.u2d.calendar.*;
import com.u2d.model.ComplexType;
import com.u2d.model.AbstractListEO;
import com.u2d.element.Field;
import com.u2d.field.AssociationField;
import com.u2d.field.IndexedField;
import com.u2d.view.swing.calendar.TimeIntervalView;
import com.u2d.view.swing.calendar.BaseEventsPnl;
import com.u2d.view.swing.calendar.BaseCalEventView;

/**
 * @author Eitan Suez
 */
public class EventsPnl extends BaseEventsPnl
{
   private Schedule _schedule;
   private AbstractListEO _leo;

   public EventsPnl(TimeIntervalView view, Schedule schedule)
   {
      super(view);
      _schedule = schedule;

      _schedule.getCalEventList().addListDataListener(this);
      
      // an eventspanel is a view of a relationallist.  it needs to synchronize
      // with changes in that list.
      Schedulable schedulable = _schedule.getSchedulable();
      Class eventTypeClass = schedulable.eventType();
      ComplexType eventType = ComplexType.forClass(eventTypeClass);
      String propName = CalEvent.schedulableFieldname(eventTypeClass);
      Field field = eventType.field(propName);
      if (field instanceof AssociationField && ((AssociationField) field).isBidirectionalRelationship())
      {
         String otherFieldName = ((AssociationField) field).getInverseFieldName();
         IndexedField otherSide = (IndexedField) schedulable.field(otherFieldName);
         _leo = (AbstractListEO) otherSide.get(schedulable);
         _leo.addListDataListener(this);
      }
      
      updateView();
   }


   public void updateView()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            removeAll();

            CalEventList events = _schedule.getCalEventList();
            for (Iterator itr = events.iterator(); itr.hasNext(); )
            {
               CalEvent event = (CalEvent) itr.next();
               
               if (!_view.getSpan().containsOrIntersects(event.timeSpan()))
               {
                  continue;
               }
               if (_leo != null && !_leo.contains(event))
               {
                  continue;
               }
               
               BaseCalEventView comp = (BaseCalEventView) event.getCalEventView(_schedule);
               comp.setupExtendSpan(_view.getTimeSheet());

               // workaround for mousewheelsupport when hovering over a calevent..
               MouseWheelListener[] listeners = _view.getScrollPane().getMouseWheelListeners();
               for (MouseWheelListener listener : listeners)
               {
                  comp.addMouseWheelListener(listener);
               }
               add(comp, event);
            }

            revalidate(); repaint();
         }
      });
   }

   public Schedule getSchedule() { return _schedule; }

   public void detach()
   {
      _schedule.getCalEventList().removeListDataListener(this);
      if (_leo != null)
      {
         _leo.removeListDataListener(this);
      }
   }

}
