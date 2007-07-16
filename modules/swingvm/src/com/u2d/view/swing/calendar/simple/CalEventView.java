/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.calendar.simple;

import com.u2d.calendar.*;
import javax.swing.*;
import java.awt.*;
import com.u2d.view.*;
import com.u2d.view.swing.calendar.BaseCalEventView;

/**
 * @author Eitan Suez
 */
public class CalEventView extends BaseCalEventView
      implements ComplexEView
{

   public CalEventView(CalEvent event)
   {
      super(event);
      setupColor();
   }
   
   protected void setupColor()
   {
      final Color backgroundColor = _event.type().colorCode();
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
               
               revalidate(); repaint();  // picks up timespan changes
            }
         });
   }

}
