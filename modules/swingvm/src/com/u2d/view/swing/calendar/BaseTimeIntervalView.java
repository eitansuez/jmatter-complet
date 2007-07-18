package com.u2d.view.swing.calendar;

import com.u2d.app.Tracing;
import com.u2d.calendar.CellResChoice;
import com.u2d.calendar.Schedulable;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.css4swing.style.ComponentStyle;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import java.util.Date;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 15, 2007
 * Time: 10:21:12 AM
 */
public abstract class BaseTimeIntervalView extends JPanel
      implements TimeIntervalView
{
   protected static Logger _log = Tracing.tracer();
   protected static Color SELECTION_BACKGROUND = new Color(0xf0f5fb);
   
   protected ITimeSheet _timesheet;

   protected JTable _table;
   protected SpanTableModel _model;
   protected int _initialRowHeight;
   protected JScrollPane _scrollPane;
   public JScrollPane getScrollPane() { return _scrollPane; }
   
   protected interface SpanTableModel extends TableModel
   {
      public void updateCellRes();
   }

   protected JLabel _label = new JLabel();
   {
      ComponentStyle.addClass(_label, "title");
   }
   public JLabel getLabel() { return _label; }

   protected void updateRowHeight()
   {
      int vpheight = _scrollPane.getViewport().getSize().height;
      int height = _table.getSize().height;
      if (vpheight >= height)
      {
         int newRowHeight = (int) ((double) vpheight / _table.getRowCount());
         newRowHeight = Math.max(_initialRowHeight, newRowHeight);
         //System.out.println("new Row height is "+newRowHeight+"; table row count is "+getRowCount());
         _table.setRowHeight(newRowHeight);
      }
      else
      {
         _table.setRowHeight(_initialRowHeight);
      }
      _table.repaint();
   }

   public void addAdjustmentListener(AdjustmentListener l)
   {
      _scrollPane.getVerticalScrollBar().addAdjustmentListener(l);
   }
   
   protected abstract void buildTable();
   protected abstract void setupDropHandler();

   private MouseWheelListener _defaultMWListener;
   private double _prevCellResMinutes;
   private Point _prevPt;
   private int _y_offset;
   
   protected void init()
   {
      buildTable();
      setupDropHandler();

      setLayout(new BorderLayout());
      _scrollPane = new JScrollPane(_table);
      add(_scrollPane, BorderLayout.CENTER);

      
      _timesheet.addPropertyChangeListener("cellResolution", new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _model.updateCellRes();
                  updateRowHeight();

                  SwingUtilities.invokeLater(new Runnable()
                  {
                     public void run()
                     {
                        if (_prevPt != null)
                        {
                           double ratio = _prevCellResMinutes / cellRes().minutes();
                           int newY = (int) (ratio * _prevPt.getY());
                           int newValue = newY - _y_offset;
                           BoundedRangeModel scrollModel = _scrollPane.getVerticalScrollBar().getModel();
                           scrollModel.setValue(newValue);
                        }
                     }
                  });
               }
            });
         }
      });
      
      MouseWheelListener[] listeners = _scrollPane.getMouseWheelListeners();
      _defaultMWListener = listeners[0];
      _scrollPane.removeMouseWheelListener(_defaultMWListener);
      _scrollPane.addMouseWheelListener(new MouseWheelListener()
      {
         public void mouseWheelMoved(MouseWheelEvent e)
         {
            if (e.isControlDown())
            {
               _prevCellResMinutes = cellRes().minutes();
               _prevPt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), _table);
               BoundedRangeModel scrollModel = _scrollPane.getVerticalScrollBar().getModel();
               _y_offset = (int) _prevPt.getY() - scrollModel.getValue();
               
               // set new cell resolution 
               boolean increaseResolution = (e.getWheelRotation() < 0);
               CellResChoice resolution = increaseResolution ? cellRes().previous() : cellRes().next();
               _timesheet.setCellResolution(resolution);
               SwingViewMechanism.getInstance().message(cellRes().toString());
            }
            else
            {
               _defaultMWListener.mouseWheelMoved(e);
            }
         }
      });
   }
   
   protected CellResChoice cellRes() { return _timesheet.getCellResolution(); }
   
   /************************************************************************
    * List of observers.
    */

   private ActionListener subscribers = null;

   /** Add a listener that's notified when the user scrolls the
    *  selector or picks a date.
    *  @see com.holub.ui.Date_selector
    */
    public synchronized void addActionListener(ActionListener l)
    {
      subscribers = AWTEventMulticaster.add(subscribers, l);
    }

   /**
    * Remove a listener.
    *  @see com.holub.ui.Date_selector
    */
    public synchronized void removeActionListener(ActionListener l)
    {
      subscribers = AWTEventMulticaster.remove(subscribers, l);
    }

   /**
    * Notify the listeners of a scroll or select
    */
   protected void fireActionEvent( Date date ) { fireActionEvent(date, null); }
   protected void fireActionEvent( Date date, Schedulable schedulable )
   {
      if (subscribers != null)
          subscribers.actionPerformed( new CalActionEvent(this, date, schedulable) );
   }

   /*****************************************************************/


   /* ** State Change Support Code ** */
   protected transient ChangeEvent _changeEvent = null;
   protected transient EventListenerList _listenerList = new EventListenerList();

   public void addChangeListener(ChangeListener l)
   {
      _listenerList.add(ChangeListener.class, l);
   }

   public void removeChangeListener(ChangeListener l)
   {
      _listenerList.remove(ChangeListener.class, l);
   }

   protected void fireStateChanged()
   {
      Object[] listeners = _listenerList.getListenerList();

      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i]==ChangeListener.class)
         {
            if (_changeEvent == null)
               _changeEvent = new ChangeEvent(this);
            ((ChangeListener)listeners[i+1]).stateChanged(_changeEvent);
         }
      }
   }
   
   /*****************************************************************/
   
   protected transient EventListenerList _dropListenerList = new EventListenerList();
   public void addDropListener(DropListener l)
   {
      _dropListenerList.add(DropListener.class, l);
   }
   public void removeDropListener(DropListener l)
   {
      _dropListenerList.remove(DropListener.class,  l);
   }
   protected void fireDropEvent(CalDropEvent dropEvent)
   {
      Object[] listeners = _dropListenerList.getListenerList();
      
      for (int i=listeners.length-2; i>=0; i-=2)
      {
         if (listeners[i]==DropListener.class)
         {
            ((DropListener)listeners[i+1]).itemDropped(dropEvent);
         }
      }
   }
   /*****************************************************************/

}
