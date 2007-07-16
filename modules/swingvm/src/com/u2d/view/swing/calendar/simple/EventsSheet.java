package com.u2d.view.swing.calendar.simple;

import com.u2d.calendar.CalEventList;
import com.u2d.view.swing.calendar.TimeIntervalView;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 21, 2007
 * Time: 1:50:03 PM
 */
public class EventsSheet extends JPanel
{
   protected static final int LAYER = 50;

   protected JLayeredPane _substrate;
   protected TimeIntervalView _view;
   protected EventsPnl _eventsPnl;
   
   public EventsSheet(TimeIntervalView view, CalEventList list)
   {
      _substrate = new JLayeredPane();
      OverlayLayout overlay = new OverlayLayout(_substrate);
      _substrate.setLayout(overlay);
      
      _view = view;
      
      _substrate.add((Component) _view);
      _substrate.setLayer((Component) _view, JLayeredPane.DEFAULT_LAYER.intValue());

      setLayout(new BorderLayout());
      add(_substrate, BorderLayout.CENTER);

      _eventsPnl = new EventsPnl(_view, list);
      _substrate.add(_eventsPnl);
      _substrate.setLayer(_eventsPnl, LAYER);
   }

   public TimeIntervalView getIntervalView() { return _view; }
   
   public void detach() { _eventsPnl.detach(); }

   public Dimension getMinimumSize()
   {
      Dimension size = getPreferredSize();
      return (new Dimension((int) (size.width*0.7), 
                            (int) (size.height * 0.7)));
   }

}
