/*
 * Created on Apr 13, 2004
 */
package com.u2d.calendar;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.view.*;
import com.u2d.type.atom.*;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.app.Tracing;
import com.u2d.list.PlainListEObject;
import java.io.*;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Expression;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

/**
 * Note:  Named so as not to conflict/confuse with java.util.Calendar
 * 
 * @author Eitan Suez
 */
public class Calendrier extends AbstractComplexEObject
                        implements Serializable, EventMaker
{
   private transient Calendarable _cal;

   public Calendrier() { setStartState(); }
   public Calendrier(Calendarable cal)
   {
      this();
      _cal = cal;
   }

   public CalEvent newDefaultCalEvent(TimeSpan span)
   {
      ComplexType eventType = ComplexType.forClass(_cal.defaultCalEventType());
      CalEvent calEvt = (CalEvent) eventType.New(null);
      calEvt.timeSpan(span);
      calEvt.schedulable((Schedulable) _schedulables.get(0));
      return calEvt;
   }
   public CalEvent newEvent(TimeSpan span) { return newDefaultCalEvent(span); }

   public Calendarable calendarable() { return _cal; }

   private Title _title = new Title("Calendar");
   public Title title() { return _title; }

   // TODO: revisit
//   public Onion commands()  // hack
//   {
//      return type().commands(getState()).subList(0, 1);
//   }

   public EView getMainView() { return getCalendarView(); }
   public ComplexEView getCalendarView() { return vmech().getCalendarView(this); }

   private List _schedulables = null;

   public AbstractListEO schedules()
   {
      AbstractListEO schedules = loadSchedules();
      _schedulables = new ArrayList();
      Schedule schedule;
      for (int i=0; i<schedules.getSize(); i++)
      {
         schedule = (Schedule) schedules.getElementAt(i);
         schedule.inCalendarContext(this);
         _schedulables.add(schedule.getSchedulable());
      }
      return schedules;
   }


   private final DateEO _position = new DateEO(new Date());
   public DateEO position() { return _position; }
   public void position(Date date)
   {
      _position.setValue(date);
   }


   /*
   a work in progress..
   
   goal is to:
      improve response time of calendar navigation from timespan to timespan
      by fetching multiple schedules together as a single query instead of 
      multiple separate queries.  revise schedule.getEventsInTimeSpan()
      query from criteria.add(expression.eq(schedule, schedule))
      to criteria.add(expression.in(schedule, schedules)), and populating 
      a map of events for each schedule as a simple cache;
      then when each event panel wants its schedule's events, you can
      just do a return (List) map.get(schedule).  q: how to distinguish between
      a cached lookup and a new one?  if the time span has changed.
   */

   private Map _events;
   private TimeSpan _span;

   List getEventsInTimeSpan(TimeSpan span, Schedule schedule)
   {
      if (_span != null && _span.containsCompletely(span))
         return subset(span, schedule);
      if (_span != null && span.equals(_span))
         return getEvents(schedule);

      Tracing.tracer().info("calendrier: fetching events for time span: "+span);

      List events = new ArrayList();
      HBMPersistenceMechanism pmech2 = (HBMPersistenceMechanism) persistor();

      Session session = pmech2.getSession();

      Class eventClass = schedule.getSchedulable().eventType();
      ComplexType eventType = ComplexType.forClass(eventClass);
      Criteria criteria = session.createCriteria(eventType.getJavaClass());
      Junction junction = Expression.conjunction();
      
      String timespanFieldname = CalEvent.timespanFieldname(eventClass);
      String schedulableFieldname = CalEvent.schedulableFieldname(eventClass);

      junction.add(Expression.in(schedulableFieldname , _schedulables));
      junction.add(Expression.ge(timespanFieldname + ".start", span.startDate()));
      junction.add(Expression.le(timespanFieldname + ".end", span.endDate()));
      criteria.add(junction);

      events = criteria.list();

      Iterator itr = events.iterator();
      _events = new HashMap();
      CalEvent event;
      while (itr.hasNext())
      {
         event = (CalEvent) itr.next();
         event.onLoad();
         addEvent(event);
      }

      _span = span;

      return getEvents(schedule);
   }

   void addEvent(CalEvent event, Schedule schedule)
   {
      getEvents(schedule).add(event);
   }

   private List getEvents(Schedule schedule)
   {
      List events = (List) _events.get(schedule);
      if (events == null)
      {
         events = new ArrayList();
         _events.put(schedule, events);
      }
      return events;
   }

   private void addEvent(CalEvent event)
   {
      Schedule schedule = event.schedulable().schedule();
      List scheduleEvents = (List) _events.get(schedule);
      if (scheduleEvents == null)
         scheduleEvents = new ArrayList();
      scheduleEvents.add(event);
      _events.put(event.schedulable().schedule(), scheduleEvents);
   }

   // return subset of events in _events that is in span
   private List subset(TimeSpan span, Schedule schedule)
   {
      List scheduleEvents = getEvents(schedule);
      Iterator itr = scheduleEvents.iterator();
      CalEvent event;
      List events = new ArrayList();
      while (itr.hasNext())
      {
         event = (CalEvent) itr.next();
         if (span.containsOrIntersects(event.timeSpan()))
         {
            events.add(event);
         }
      }
      return events;
   }


   // =======

   private AbstractListEO _schedules = new PlainListEObject(Schedule.class);

   private AbstractListEO loadSchedules()
   {
      AbstractListEO schedulables = _cal.schedulables();
      if (schedulables == null)
      {
         return new PlainListEObject(Schedulable.class);
      }

      // ensure that list of schedules is in sync with list of resources
      // (e.g. rooms, providers)
      ScheduleSynchronizer _scheduleSynchronizer = new ScheduleSynchronizer();
      schedulables.addListDataListener(_scheduleSynchronizer);
      _scheduleSynchronizer.contentsChanged(null);
      
      return _schedules;
   }

   class ScheduleSynchronizer implements ListDataListener
   {
      public void intervalAdded(ListDataEvent e)
      {
         Schedulable schedulable = null;
         for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
         {
            schedulable = (Schedulable) _cal.schedulables().getElementAt(i);
            _schedules.add(schedulable.schedule());
         }
      }

      public void intervalRemoved(ListDataEvent e)
      {
         Schedulable schedulable = null;
         for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
         {
            schedulable = (Schedulable) _cal.schedulables().getElementAt(i);
            _schedules.remove(schedulable.schedule());
         }
      }

      public void contentsChanged(ListDataEvent e)
      {
         Schedulable schedulable;  Schedule schedule;
         List list = new ArrayList();

         for (Iterator schedsItr = _cal.schedulables().iterator(); 
               schedsItr.hasNext(); )
         {
            schedulable = (Schedulable) schedsItr.next();
            schedule = schedulable.schedule();
            list.add(schedule);
         }
         _schedules.setItems(list);
      }
   }



}
