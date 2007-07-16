package com.u2d.view.swing.calendar.simple;

import com.u2d.calendar.CalEventList;
import com.u2d.view.swing.calendar.TimeIntervalView;
import com.u2d.view.swing.calendar.BaseEventsSheet;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 21, 2007
 * Time: 1:50:03 PM
 */
public class EventsSheet extends BaseEventsSheet
{
   protected static final int LAYER = 50;

   protected EventsPnl _eventsPnl;
   
   public EventsSheet(TimeIntervalView view, CalEventList list)
   {
      super(view);

      _eventsPnl = new EventsPnl(_view, list);
      _substrate.add(_eventsPnl);
      _substrate.setLayer(_eventsPnl, LAYER);
   }

   public void detach() { _eventsPnl.detach(); }

}
