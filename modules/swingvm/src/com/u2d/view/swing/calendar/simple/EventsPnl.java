/*
 * Created on Apr 12, 2004
 */
package com.u2d.view.swing.calendar.simple;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import com.u2d.calendar.*;
import com.u2d.pubsub.AppEventType;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import com.u2d.view.swing.calendar.TimeIntervalView;
import com.u2d.view.swing.calendar.BaseEventsPnl;
import com.u2d.view.swing.calendar.BaseCalEventView;

/**
 * @author Eitan Suez
 */
public class EventsPnl extends BaseEventsPnl
{
   private CalEventList _calEventList;
   
   public EventsPnl(TimeIntervalView view, CalEventList calEventList)
   {
      super(view);
      _calEventList = calEventList;

      _calEventList.addListDataListener(this);
      _calEventList.type().addAppEventListener(AppEventType.SAVE, new AppEventListener() {
         public void onEvent(AppEvent evt)
         {
            updateView();  // times may have changed..
         }
      });
      updateView();
   }

   public void updateView()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            removeAll();

            for (Iterator itr = _calEventList.iterator(); itr.hasNext(); )
            {
               CalEvent event = (CalEvent) itr.next();
               
               if (!_view.getSpan().containsOrIntersects(event.timeSpan()))
               {
                  continue;
               }
               
               BaseCalEventView comp = (BaseCalEventView) event.getCalEventView();
               comp.setupExtendSpan(_view.getTimeSheet());

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

   public void detach()
   {
      _calEventList.removeListDataListener(this);
   }

}
