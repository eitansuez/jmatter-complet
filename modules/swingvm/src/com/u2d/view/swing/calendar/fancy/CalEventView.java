/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.calendar.fancy;

import com.u2d.calendar.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.u2d.view.*;
import com.u2d.view.swing.calendar.BaseCalEventView;

/**
 * @author Eitan Suez
 */
public class CalEventView extends BaseCalEventView
      implements ComplexEView
{

   public CalEventView(CalEvent event, Schedule schedule)
   {
      super(event);
      setupColor(schedule);
      _header.addMouseListener(_layerController);
      _body.addMouseListener(_layerController);
   }
   
   MouseAdapter _layerController = new MouseAdapter()
   {
      public void mouseClicked(MouseEvent e)
      {
         EventsSheet sheet = (EventsSheet) 
               SwingUtilities.getAncestorOfClass(EventsSheet.class,  CalEventView.this);
         if (sheet != null)
         {
            sheet.bringScheduleToFront(_event);
         }
      }
   };
   
   private void setupColor(Schedule schedule)
   {
      if (schedule == null) return;
      
      final Color backgroundColor = schedule.getColor();
      _header.setBackground(backgroundColor);
      _body.setupColor(backgroundColor);
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run()
            {
               _header.updateText();
               _body.setText(_event.calTitle().toString());
               _header.setFont(_body.getFont());
               
               if (_event.schedulable() != null)
               {
                  Schedule schedule = _event.schedulable().schedule();
                  setupColor(schedule);
               }
               revalidate(); repaint();  // picks up timespan changes
            }
         });
   }

}