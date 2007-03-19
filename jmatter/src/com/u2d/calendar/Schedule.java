/*
 * Created on Apr 13, 2004
 */
package com.u2d.calendar;

import com.u2d.model.*;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.type.atom.*;
import com.u2d.view.*;
import com.u2d.element.Field;
import com.u2d.ui.UIUtils;
import java.awt.Color;

/**
 * @author Eitan Suez
 */
public class Schedule extends AbstractComplexEObject implements EventMaker, DateTimeBounded
{
   private static Color[] COLORS = {Color.red, Color.blue, Color.green, 
         Color.cyan, Color.magenta, Color.yellow };
   private static int COLOR_IDX = 0;
   
   private Schedulable _schedulable;
   private Color _color = UIUtils.lighten(COLORS[COLOR_IDX++ % COLORS.length]);
   private Field _colorField;
   private DateTimeBounds _bounds = new DateTimeBounds();
   private Calendrier _calendrier = null;
   private int _layer;
   
   private final CalEventList _events = new CalEventList();

   public Schedule() { }

   public Schedule(Schedulable schedulable)
   {
      this();
      
      _schedulable = schedulable;
      if (_schedulable.type().hasFieldOfType(ColorEO.class))
      {
         _colorField = _schedulable.type().firstFieldOfType(ColorEO.class);
      }
      
      _events.setSchedulable(_schedulable);
      
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
            }
         });

      setStartState();
   }
   
   public Schedulable getSchedulable() { return _schedulable; }
   public void setSchedulable(Schedulable sched)
   {
      Schedulable oldSched = _schedulable;
      _schedulable = sched;
      firePropertyChange("schedulable", oldSched, _schedulable);
   }
   
   public Color getColor()
   {
      if (_colorField != null)
      {
         ColorEO colorEO = (ColorEO) _colorField.get(_schedulable);
         return colorEO.colorValue();
      }
      return _color;
   }
   
   public CalEventList getCalEventList() { return _events; }

   public DateTimeBounds bounds() { return _bounds; }
   public void bounds(DateTimeBounds bounds) { _bounds = bounds; }

   void inCalendarContext(Calendrier calendrier) { _calendrier = calendrier; }
   private boolean inCalendarContext() { return _calendrier != null; }

   public int getLayer() { return _layer; }
   public void setLayer(int layer) { _layer = layer; }


   public CalEvent newEvent(TimeSpan span)
   {
      Class eventClass = _schedulable.eventType();
      ComplexType eventType = ComplexType.forClass(eventClass);
      CalEvent calEvent = (CalEvent) eventType.instance();
      calEvent.timeSpan(span);
      calEvent.association(calEvent.schedulableFieldname()).set(_schedulable);
      return calEvent;
   }
   
   public void fetchEvents(TimeSpan span)
   {
      _events.setSpan(span);
   }
   
   public EView getMainView() { return getScheduleView(); }
   public ComplexEView getScheduleView() { return vmech().getScheduleView(this); }
   

   public Title title() { return _schedulable.title(); }

   
   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof Schedule)) return false;
      Schedule s = (Schedule) obj;
      return _schedulable.equals(s.getSchedulable());
   }

   public int hashCode() { return _schedulable.hashCode(); }
}
