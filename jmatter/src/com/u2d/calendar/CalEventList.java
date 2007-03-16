package com.u2d.calendar;

import com.u2d.model.*;
import com.u2d.list.Navigable;
import com.u2d.find.Query;
import static com.u2d.pubsub.AppEventType.DELETE;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TimeSpan;
import com.u2d.view.EView;
import com.u2d.app.Tracing;
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
      implements Navigable
{
   private Query _query;
   private final TimeSpan _span = new TimeSpan();

   public CalEventList() {}
   public CalEventList(Query query, TimeSpan span)
   {
      setQuery(query, span);
   }
   
   public Query getQuery() { return _query; }
   public TimeSpan getSpan() { return _span; }
   
   public synchronized void setQuery(Query query, TimeSpan span)
   {
      _query = query;
      fetchSpan(span);
   }

   private Criteria constrainBySpan()
   {
      Criteria criteria = _query.getCriteria();

      Junction junction = Expression.conjunction();
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
      if (_span != null && (_span.equals(span) || _span.containsCompletely(span)))
      {
         fireContentsChanged(this, 0, getSize());
         return;
      }
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

   public ComplexType type() { return _query.getQueryType(); }
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

   public EView getView() { return vmech().getListView(this); }
   public EView getMainView() { return getView(); }

}
