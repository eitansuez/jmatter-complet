package com.u2d.view.swing.simplecal;

import com.u2d.view.ListEView;
import com.u2d.calendar.CalEventList;
import com.u2d.calendar.DateTimeBounds;
import com.u2d.model.EObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 11, 2007
 * Time: 7:29:54 PM
 */
public class CalendarListView extends JPanel
      implements ListEView
{
   private CalEventList _list;
   private TimeSheet _timeSheet;

   public CalendarListView(CalEventList list)
   {
      _list = list;
      _timeSheet = new TimeSheet(_list, new DateTimeBounds());
      
      setLayout(new BorderLayout());
      add(_timeSheet, BorderLayout.CENTER);
   }

   public void stateChanged(ChangeEvent e) { }
   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

   public EObject getEObject() { return _list; }
   public void detach() { _timeSheet.detach(); }
   public boolean isMinimized() { return false; }
}
