package com.u2d.calendar;

import com.u2d.model.*;
import com.u2d.list.Navigable;
import com.u2d.find.*;
import com.u2d.find.inequalities.IdentityInequality;
import static com.u2d.pubsub.AppEventType.DELETE;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TimeSpan;
import com.u2d.view.EView;
import com.u2d.app.Tracing;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import com.u2d.element.Command;
import com.u2d.pattern.Onion;
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
      implements Navigable, QueryReceiver
{
   private Query _query, _previousQuery;
   private final TimeSpan _span = new TimeSpan();

   public CalEventList() {}
   
   public void setSchedulable(Schedulable schedulable)
   {
      Class eventClass = schedulable.eventType();
      ComplexType eventType = ComplexType.forClass(eventClass);
      
      String schedulableFieldname = CalEvent.schedulableFieldname(eventClass);
      FieldPath path = new FieldPath(eventType.field(schedulableFieldname).fullPath());
      QuerySpecification spec = 
            new QuerySpecification(path, 
                                   new IdentityInequality().new Equals(),
                                   schedulable);
      com.u2d.find.Query query = new SimpleQuery(eventType, spec);
      setQuery(query);
   }
   
   public CalEventList(Query query, TimeSpan span)
   {
      setQuery(query, span);
   }
   
   public Query getQuery() { return _query; }
   public TimeSpan getSpan() { return _span; }
   
   public void setQuery(Query query)
   {
      setQuery(query, _span);
   }
   public synchronized void setQuery(Query query, TimeSpan span)
   {
      _previousQuery = _query;
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
      // optimization: when switch from weekview to dayview, already have events..
      if ( (_previousQuery != null && _previousQuery.equals(_query)) &&
           (_span != null && (_span.equals(span) || _span.containsCompletely(span))) )
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

   public EView getView() { return vmech().getListView(this); }
   public EView getMainView() { return getView(); }

   // TODO: fix this..
   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      return type().New(cmdInfo);
   }
   private static Onion _cmds;
   static
   {
      _cmds = Harvester.simpleHarvestCommands(CalEventList.class,
                                                   new Onion(), false, null);
   }
   public Onion commands() { return _cmds; }
   public Command command(String commandName)
   {
      return (Command) _cmds.find(Command.finder(commandName));
   }

   
}
