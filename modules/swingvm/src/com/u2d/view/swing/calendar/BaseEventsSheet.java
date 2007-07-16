package com.u2d.view.swing.calendar;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 16, 2007
 * Time: 5:15:00 PM
 */
public abstract class BaseEventsSheet extends JPanel
{
   protected JLayeredPane _substrate;
   protected TimeIntervalView _view;
   
   protected BaseEventsSheet(TimeIntervalView view)
   {
      _substrate = new JLayeredPane();
      OverlayLayout overlay = new OverlayLayout(_substrate);
      _substrate.setLayout(overlay);
      
      _view = view;
      
      _substrate.add((Component) _view);
      _substrate.setLayer((Component) _view, JLayeredPane.DEFAULT_LAYER.intValue());

      setLayout(new BorderLayout());
      add(_substrate, BorderLayout.CENTER);
   }

   public TimeIntervalView getIntervalView() { return _view; }
   
   public abstract void detach();

   public Dimension getMinimumSize()
   {
      Dimension size = getPreferredSize();
      return (new Dimension((int) (size.width*0.7), 
                            (int) (size.height * 0.7)));
   }

}
