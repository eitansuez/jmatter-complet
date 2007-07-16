/*
 * Created on Apr 12, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.u2d.calendar.*;
import com.u2d.model.ComplexType;
import com.u2d.model.AbstractListEO;
import com.u2d.element.Field;
import com.u2d.field.AssociationField;
import com.u2d.field.IndexedField;

/**
 * @author Eitan Suez
 */
public class EventsPnl extends JPanel implements AdjustmentListener, ListDataListener,
  TableColumnModelListener, ChangeListener
{
   private TimeIntervalView _view;
   private Schedule _schedule;
   private java.awt.LayoutManager _layout;
   
   private AbstractListEO _leo;

   public EventsPnl(TimeIntervalView view, Schedule schedule)
   {
      _view = view;
      _schedule = schedule;

      _layout = new PositionedLayout(_view);
      setLayout(_layout);
      setOpaque(false);

      _view.addAdjustmentListener(this);

      _schedule.getCalEventList().addListDataListener(this);
      
      _view.getSpan().addChangeListener(this);
      
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


   public void stateChanged(ChangeEvent e) { updateView(); }

   public void intervalAdded(ListDataEvent e) { updateView(); }
   public void intervalRemoved(ListDataEvent e) { updateView(); }
   public void contentsChanged(ListDataEvent e) { updateView(); }

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
               
               JComponent comp = (JComponent) event.getCalEventView(_schedule);
               
               // workaround for mousewheelsupport when hovering over a calevent..
               MouseWheelListener[] listeners = _view.getScrollPane().getMouseWheelListeners();
               for (int i=0; i<listeners.length; i++)
               {
                  comp.addMouseWheelListener(listeners[i]);
               }
               add(comp, event);
            }

            revalidate(); repaint();
         }
      });
   }

   public Schedule getSchedule() { return _schedule; }

   // since the scrollbar is tied to the weekview, need to do some work
   // to ensure that this panel is also driven by it
   public void adjustmentValueChanged(AdjustmentEvent evt)
   {
      _layout.layoutContainer(this);
   }

   // implementation of tablecolumnmodellistener
   public void columnMoved(TableColumnModelEvent evt)
   {
      _layout.layoutContainer(this);
   }
   public void columnMarginChanged(ChangeEvent evt)
   {
      _layout.layoutContainer(this);
   }
   public void columnAdded(TableColumnModelEvent evt) { }
   public void columnRemoved(TableColumnModelEvent evt) { }
   public void columnSelectionChanged(ListSelectionEvent evt) { }

   public void detach()
   {
      _schedule.getCalEventList().removeListDataListener(this);
      if (_leo != null)
      {
         _leo.removeListDataListener(this);
      }
   }

}
