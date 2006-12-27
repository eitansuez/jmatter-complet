package com.u2d.view.swing.calendar;

import com.u2d.type.atom.TimeSpan;
import com.u2d.calendar.CalEvent;
import com.u2d.calendar.EventMaker;
import com.u2d.element.Field;
import com.u2d.model.ComplexType;
import javax.swing.*;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 18, 2006
 * Time: 2:51:19 PM
 */
public class CalendarDropHandler implements DropListener
{
   EventMaker _maker;
   
   CalendarDropHandler(EventMaker maker)
   {
      _maker = maker;
   }
   
   public void itemDropped(final CalDropEvent evt)
   {
      TimeSpan span = new TimeSpan(evt.getTime(), CalEvent.DEFAULT_DURATION);
      final CalEvent calEvent = _maker.newEvent(span);
      
      if (evt.getSchedulable() != null)
         calEvent.schedulable(evt.getSchedulable());

      for (Iterator itr = calEvent.childFields().iterator(); itr.hasNext(); )
      {
         Field field = (Field) itr.next();
         ComplexType transferObjectType = evt.getTransferObject().type();
         ComplexType fieldType = field.fieldtype();
         if (fieldType != null && fieldType.isAssignableFrom(transferObjectType))
//         if (transferObjectType.equals(fieldType))
         {
            field.set(calEvent, evt.getTransferObject());

            new Thread()
            {
               public void run()
               {
                  calEvent.save();
                  SwingUtilities.invokeLater(new Runnable()
                  {
                     public void run()
                     {
                        evt.getDropTargetDropEvent().dropComplete(true);
                     }
                  });
               }
            }.start();

            return;
         }
      }
      evt.getDropTargetDropEvent().rejectDrop();
   }
}
