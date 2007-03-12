/*
 * Created on Apr 13, 2004
 */
package com.u2d.calendar;

import com.u2d.app.*;
import com.u2d.model.*;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.type.atom.*;
import com.u2d.view.*;
import com.u2d.element.Field;
import com.u2d.ui.UIUtils;
import java.util.*;
import java.awt.Color;
import org.hibernate.*;
import org.hibernate.criterion.*;

/**
 * @author Eitan Suez
 */
public class Schedule extends AbstractComplexEObject implements EventMaker, DateTimeBounded
{
   private Schedulable _schedulable;
   
   private static Color[] COLORS = {Color.red, Color.blue, Color.green, 
         Color.cyan, Color.magenta, Color.yellow };
   private static int COLOR_IDX = 0;
   
   private Color _color = UIUtils.lighten(COLORS[COLOR_IDX++ % COLORS.length]);
   private Field _colorField;

   public Schedule() {}
   
   public Schedule(Schedulable schedulable)
   {
      _schedulable = schedulable;
      if (_schedulable.type().hasFieldOfType(ColorEO.class))
      {
         _colorField = _schedulable.type().firstFieldOfType(ColorEO.class);
      }
      
      Class eventClass = _schedulable.eventType();
      ComplexType eventType = ComplexType.forClass(eventClass);
      eventType.addAppEventListener(CREATE, new AppEventListener()
         {
            public void onEvent(AppEvent appEvt)
            {
               CalEvent event = (CalEvent) appEvt.getEventInfo();
               if (inCalendarContext())
               {
                  _calendrier.addEvent(event, Schedule.this);
               }
               else
               {
                  _events.add(event);
               }
               fireStateChanged();
            }
         });
      
      setStartState();
   }
   
   public EView getMainView() { return getScheduleView(); }
   public ComplexEView getScheduleView() { return vmech().getScheduleView(this); }
   
   public Schedulable getSchedulable() { return _schedulable; }
   public void setSchedulable(Schedulable sched)
   {
      Schedulable oldSched = _schedulable;
      _schedulable = sched;
      firePropertyChange("schedulable", oldSched, _schedulable);
   }
   

   private List<CalEvent> _events;
   private TimeSpan _span;
   
   public List getEventsInTimeSpan(TimeSpan span)
   {
      // an attempt to flatten multiple db queries per schedule into a single query
      if (inCalendarContext())
         return _calendrier.getEventsInTimeSpan(span, this);

      if (_span != null && _span.containsCompletely(span))
         return subset(span);
      if (_span != null && span.equals(_span))
         return _events;

      Tracing.tracer().info("schedule:  fetching events for time span: "+span);
      
      List<CalEvent> events = new ArrayList<CalEvent>();
      HBMPersistenceMechanism pmech2 = (HBMPersistenceMechanism) persistor();

      Session session = pmech2.getSession();
      
      Class eventClass = _schedulable.eventType();
      ComplexType eventType = ComplexType.forClass(eventClass);
      Criteria criteria = session.createCriteria(eventType.getJavaClass());
      Junction junction = Expression.conjunction();
      
      String timespanFieldname = CalEvent.timespanFieldname(eventClass);
      String schedulableFieldname = CalEvent.schedulableFieldname(eventClass);
      
      junction.add(Expression.eq(schedulableFieldname , _schedulable));
      junction.add(Expression.ge(timespanFieldname + ".start", span.startDate()));
      junction.add(Expression.le(timespanFieldname + ".end", span.endDate()));
      criteria.add(junction);
      
      events = criteria.list();
      
      for (CalEvent evt : events)
      {
         evt.onLoad();
      }
      
      _span = span;
      _events = events;
      
      return events;
   }
   
   // return subset of events in _events that is in span
   private List subset(TimeSpan span)
   {
      List<CalEvent> events = new ArrayList<CalEvent>();
      for (CalEvent evt : _events)
      {
         if (span.containsOrIntersects(evt.timeSpan()))
         {
            events.add(evt);
         }
      }
      return events;
   }
   
   public Title title() { return _schedulable.title(); }

   public Color getColor()
   {
      if (_colorField != null)
      {
         ColorEO colorEO = (ColorEO) _colorField.get(_schedulable);
         return colorEO.colorValue();
      }
      return _color;
   }

   public CalEvent newEvent(TimeSpan span)
   {
      Class eventClass = _schedulable.eventType();
      ComplexType eventType = ComplexType.forClass(eventClass);
      CalEvent calEvent = (CalEvent) eventType.instance();
      calEvent.timeSpan(span);
      calEvent.association(calEvent.schedulableFieldname()).set(_schedulable);
      return calEvent;
   }
   
   private DateTimeBounds _bounds;
   public DateTimeBounds bounds() { return _bounds; }
   public void bounds(DateTimeBounds bounds) { _bounds = bounds; }

   Calendrier _calendrier = null;
   void inCalendarContext(Calendrier calendrier) { _calendrier = calendrier; }
   private boolean inCalendarContext() { return _calendrier != null; }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof Schedule)) return false;
      Schedule s = (Schedule) obj;
      return _schedulable.equals(s.getSchedulable());
   }

   public int hashCode() { return _schedulable.hashCode(); }
}
