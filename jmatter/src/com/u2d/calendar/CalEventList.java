package com.u2d.calendar;

import com.u2d.model.*;
import com.u2d.list.Navigable;
import com.u2d.find.*;
import static com.u2d.pubsub.AppEventType.DELETE;
import static com.u2d.pubsub.AppEventType.CREATE;
import static com.u2d.pubsub.AppEventType.SAVE;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TimeSpan;
import com.u2d.view.EView;
import com.u2d.app.Tracing;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Expression;
import javax.swing.event.ListDataListener;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 15, 2007
 * Time: 3:32:37 PM
 * 
 * Modeled somewhat from CriteriaListEO..
 */
public class CalEventList extends AbstractListEO
      implements Navigable, QueryReceiver, EventManager
{
   private Query _query;
   private final TimeSpan _span = new TimeSpan();
   private Schedulable _schedulable;

   private AppEventListener _addOrSaveListener =
     new AppEventListener()
       {
          public void onEvent(AppEvent evt)
          {
             CalEvent calevt = (CalEvent) evt.getEventInfo();
             if (_span.containsOrIntersects(calevt.timeSpan()))
             {
                add(calevt);
             }
             else
             {
                remove(calevt);
             }
          }
       };

   public CalEventList() { }

   public CalEventList(Query query, TimeSpan span)
   {
      this();
      setQuery(query, span);
      type().addAppEventListener(CREATE, _addOrSaveListener);
      type().addAppEventListener(SAVE, _addOrSaveListener);
   }
   
   public void setSchedulable(Schedulable schedulable)
   {
      _schedulable = schedulable;
      _query = new SimpleQuery(ComplexType.forClass(_schedulable.eventType()));
   }

   public Query getQuery() { return _query; }
   public TimeSpan getSpan() { return _span; }
   
   public void setQuery(Query query)
   {
      setQuery(query, _span);
   }

   public synchronized void setQuery(Query query, TimeSpan span)
   {
      _query = query;
      fetchSpan(span);
   }
   public synchronized void setSpan(TimeSpan span)
   {
      fetchSpan(span);
   }

   private Criteria constrainBySpan()
   {
      Criteria criteria = _query.getCriteria();
      Junction junction = Expression.conjunction();

      if (_schedulable != null)
      {
         Class eventClass = _schedulable.eventType();
         String schedulableFieldname = CalEvent.schedulableFieldname(eventClass);
         junction.add(Expression.eq(schedulableFieldname , _schedulable));
      }

      String timespanFieldname = CalEvent.timespanFieldname(getJavaClass());
      junction.add(Expression.ge(timespanFieldname + ".start", _span.startDate()));
      junction.add(Expression.le(timespanFieldname + ".end", _span.endDate()));
      criteria.add(junction);
      
      return criteria;
   }
   
   private void fetchCurrentSpan()
   {
      Tracing.tracer().info("CalEventList:  fetching events for time span: "+_span);
      
      Criteria timeconstrained = constrainBySpan();
      java.util.List items = timeconstrained.list();

      for (int i=0; i<items.size(); i++)
      {
         ((ComplexEObject) items.get(i)).onLoad();
      }
      setItems(items);
   }

   public void fetchSpan(TimeSpan span)
   {
      _span.setValue(span);
      fetchCurrentSpan();
   }

   public void nextPage()
   {
      _span.setValue(_span.next());
      fetchCurrentSpan();
   }
   public void previousPage()
   {
      _span.setValue(_span.previous());
      fetchCurrentSpan();
   }

   public ComplexType queryType() { return _query.getQueryType(); }
   public ComplexType type() { return queryType(); }
   public Class getJavaClass() { return type().getJavaClass(); }

   public int getSize() { return _items.size(); }
   public int getTotal() { return getSize(); } 

   public EObject makeCopy()
   {
      return new CalEventList(_query, _span);
   }
   
   public void removeListDataListener(ListDataListener l)
   {
      super.removeListDataListener(l);
      if (_listDataListenerList.getListenerCount() == 0)
      {
        // remove ondelete listener from items
        for (Iterator itr = _items.iterator(); itr.hasNext(); )
        {
           ComplexEObject ceo = (ComplexEObject) itr.next();
           ceo.removeAppEventListener(DELETE, this);
        }
      }
   }
   
   public Title title()
   {
      StringEO name = _query.getName();
      if (name.isEmpty())
         return super.title();
      return name.title().appendParens(""+getTotal());
   }

   public boolean isEmpty() { return _items.isEmpty(); }

   public EView getMainView() { return getView(); }
   public EView getView()
   {
      return vmech().getListViewAsCalendar(this);
   }


   // EventManager implementation
   public CalEvent newEvent(TimeSpan span)
   {
      ComplexType eventType = _query.getQueryType();
      if (_schedulable != null)
      {
         Class eventClass = _schedulable.eventType();
         eventType = ComplexType.forClass(eventClass);
      }
      final CalEvent calEvent = (CalEvent) eventType.instance();
      calEvent.timeSpan(span);
      if (_schedulable != null)
         calEvent.schedulable(_schedulable);
      
      return calEvent;
   }
   
   public void fetchEvents(TimeSpan span) { setSpan(span); }


   // See NullAssociation for comments
   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      return New(cmdInfo, type());
   }
   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo, ComplexType type)
   {
      return type.New(cmdInfo);
   }
   public ComplexType baseType()
   {
      return type().baseType();
   }

}
