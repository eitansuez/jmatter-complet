/*
 * Created on Sep 17, 2003
 */
package com.u2d.view.swing.calendar;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.io.IOException;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import com.u2d.calendar.CalEvent;
import com.u2d.type.atom.*;
import com.u2d.ui.CustomLabel;
import com.u2d.model.ComplexEObject;

/**
 * @author Eitan Suez
 */
public class WeekView extends JPanel implements TimeIntervalView
{
   private static final String[] WEEKS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
   private static DateFormat COLHEADER_FORMATTER= new SimpleDateFormat("EEE, MMM dd");

   private static int COLUMN_WIDTH = 100;
   private static int WEEKEND_COLUMN_WIDTH = 85;
   private static int FIRST_COLUMN_WIDTH = 65;
   private static Color SELECTION_BACKGROUND = new Color(0xf0f5fb);

   private static TimeInterval WEEK_INTERVAL = new TimeInterval(Calendar.DATE, 7);
   private static TimeInterval DAY_INTERVAL = new TimeInterval(Calendar.HOUR, 12);

   private static java.text.SimpleDateFormat LABEL_DATE_FORMATTER =
      new java.text.SimpleDateFormat("MMMM dd yyyy");

   private TimeSpan _daySpan;
   private TimeInterval _cellRes = new TimeInterval(Calendar.MINUTE, 30);
   private TimeSpan _weekSpan;

   private JTable _table;
   private WeekViewModel _model;
   private int _initialRowHeight;
   private JScrollPane _scrollPane;

   private DateEO _eo;
   private JLabel _label = new CustomLabel(16.0f, JLabel.CENTER);

   public WeekView(DateEO eo)
   {
      _eo = eo;
      _eo.addChangeListener(new javax.swing.event.ChangeListener()
         {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
               adjustDatesAndTimes();
               _label.setText("Week of "+LABEL_DATE_FORMATTER.format(_weekSpan.startDate()));

               for (int i=1; i<_model.getColumnCount(); i++)
                  _table.getColumn(""+i).setHeaderValue(_model.getColumnName(i));

               Calendar cal = Calendar.getInstance();
               cal.setTime(_eo.dateValue());
               int column = cal.get(Calendar.DAY_OF_WEEK);
               _table.setColumnSelectionInterval(column, column);
               repaint();

               fireStateChanged();
           }
         });

      adjustDatesAndTimes();
      _label.setText("Week of "+LABEL_DATE_FORMATTER.format(_weekSpan.startDate()));

      buildTable();
      setupDropHandler();

      setLayout(new BorderLayout());
      _scrollPane = new JScrollPane(_table);
      add(_scrollPane, BorderLayout.CENTER);
   }

   private void buildTable()
   {
      _model = new WeekViewModel();
      _table = new JTable();

      _table.setAutoCreateColumnsFromModel(false);
      _table.setModel(_model);

      TableColumn column = new TableColumn(0, FIRST_COLUMN_WIDTH,
                                           new RowHeaderCellRenderer(), null);
      column.setMinWidth(FIRST_COLUMN_WIDTH);
      column.setMaxWidth(FIRST_COLUMN_WIDTH);
      column.setIdentifier("times");
      _table.addColumn(column);

      DefaultTableCellRenderer renderer = null;
      for (int i=1; i<_model.getColumnCount(); i++)
      {
         int width = (i==Calendar.SATURDAY || i==Calendar.SUNDAY) ?
               WEEKEND_COLUMN_WIDTH : COLUMN_WIDTH;
         renderer = new DefaultTableCellRenderer();
         column = new TableColumn(i, width, renderer, null);
         column.setIdentifier(""+i);
         _table.addColumn(column);
      }

      _table.setGridColor(Color.lightGray);
      _table.setShowGrid(true);
      _table.setRowSelectionAllowed(false);
      _table.setColumnSelectionAllowed(true);
      _table.getTableHeader().setReorderingAllowed(false);
      _table.setSelectionBackground(SELECTION_BACKGROUND);

      // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  // no good
      // no setting to autoresize cells..unfortunate.. must somehow set height of table programmatically as container 
      // changes size??  or extend JTable layout management?  this kind of sucks.  at this point i'm wondering 
      // whether using a JTable was the right thing to do...

      _table.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent evt)
         {
            if (evt.getClickCount() == 2)
            {
               int colidx = _table.getSelectedColumn();
               if (colidx == 0) return;  // dblclick on times labels does nothing
               int rowidx = _table.getSelectedRow();
               // System.out.println("noting action on cell "+rowidx+","+colidx);

               Calendar cal = getDateTimeForCellCoordinates(rowidx, colidx);

               fireActionEvent(cal.getTime());
            }
         }
      });

      _initialRowHeight = _table.getRowHeight();

      _table.addComponentListener(new ComponentAdapter()
      {
         public void componentResized(ComponentEvent evt)
         {
            updateRowHeight();
         }
      });
   }

   private Calendar getDateTimeForCellCoordinates(int rowidx, int colidx)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(_daySpan.startDate());
      cal.add(Calendar.DATE, colidx-1);
           // (colidx of 1 is sunday, first day of week, add nothing)
      cal.add(Calendar.MINUTE, rowidx*(int)_cellRes.getMilis()/(1000*60));
      return cal;
   }


   private void setupDropHandler()
   {
      DropTarget dropTarget = new DropTarget();
      try
      {
         dropTarget.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(final java.awt.dnd.DropTargetDropEvent dropTargetDropEvent)
            {
               Transferable t = dropTargetDropEvent.getTransferable();

               Object transferObject = null;
               try
               {
                  DataFlavor flavor = t.getTransferDataFlavors()[0];
                  transferObject = t.getTransferData(flavor);
               }
               catch (UnsupportedFlavorException ex)
               {
                  System.err.println("UnsupportedFlavorException: "+ex.getMessage());
                  ex.printStackTrace();
                  dropTargetDropEvent.rejectDrop();
               }
               catch (IOException ex)
               {
                  System.err.println("IOException: "+ex.getMessage());
                  ex.printStackTrace();
                  dropTargetDropEvent.rejectDrop();
               }

               Point location = dropTargetDropEvent.getLocation();
               int colIndex = _table.columnAtPoint(location);
               int rowIndex = _table.rowAtPoint(location);
               Calendar slot = getDateTimeForCellCoordinates(rowIndex,  colIndex);

               if (transferObject instanceof CalEvent)
               {
                  final CalEvent calEvent = (CalEvent) transferObject;

                  TimeSpan moved = calEvent.timeSpan().move(slot.getTime());
                  calEvent.timeSpan(moved);  // update time span for cal event
                  calEvent.fireStateChanged();

                  new Thread()
                  {
                     public void run()
                     {
                        calEvent.save();
                        SwingUtilities.invokeLater(new Runnable()
                        {
                           public void run()
                           {
                              dropTargetDropEvent.dropComplete(true);
                           }
                        });
                     }
                  }.start();

               }
               else if (transferObject instanceof ComplexEObject)
               {
                  fireDropEvent(new CalDropEvent(this, slot.getTime(), null, 
                                                 (ComplexEObject) transferObject,
                                                 dropTargetDropEvent));
               }
               else
               {
                  dropTargetDropEvent.rejectDrop();
                  return;
               }
            }
         });
         _table.setDropTarget(dropTarget);
      }
      catch (TooManyListenersException ex)
      {
         System.err.println("TooManyListenersException: "+ex.getMessage());
         ex.printStackTrace();
      }
   }


   private void adjustDatesAndTimes()
   {
      Calendar startOfWeek = Calendar.getInstance();
      startOfWeek.setTime(_eo.dateValue());
      startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
      startOfWeek.set(Calendar.HOUR_OF_DAY, 0);
      startOfWeek.set(Calendar.MINUTE, 0);
      startOfWeek.set(Calendar.SECOND, 0);
      //System.out.println("beginning of week is "+startOfWeek.getTime());

      TimeEO startHr = new TimeEO(startOfWeek.getTimeInMillis());
      startHr.set(Calendar.HOUR_OF_DAY, 7);

      _weekSpan = new TimeSpan(startOfWeek.getTime(), WEEK_INTERVAL);
      _daySpan = new TimeSpan(startHr.dateValue(), DAY_INTERVAL); // 7 AM - 7 PM
   }

   private void updateRowHeight()
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

   public TimeSpan getSpan() { return _weekSpan; }

   public TimeInterval getCellRes() { return _cellRes; }
   public void setCellResolution(TimeInterval cellRes)
   {
      _cellRes = cellRes;
      _model.updateCellRes();
      updateRowHeight();
   }

   public Rectangle getBounds(CalEvent event)
   {
      TimeSpan span = event.timeSpan();
      Date startDate = _daySpan.startDate();
      TimeSpan daySpan = new TimeSpan(startDate, span.startDate());
      double distance = daySpan.distance(_cellRes);
      distance %= _model._distance;

      int rowHeight = _table.getRowHeight();
      int yPos = (int) (distance * rowHeight) + _table.getTableHeader().getHeight();
      //System.out.println("yPos: "+yPos+"; distance: "+distance);

      int eventHeight = (int) ( ( span.duration().getMilis() * rowHeight ) / _cellRes.getMilis() );
      eventHeight = Math.max(eventHeight, rowHeight);

      Calendar cal = Calendar.getInstance();
      cal.setTime(span.startDate());
      int dayofweek = cal.get(Calendar.DAY_OF_WEEK);

      int currentFirstColWidth = _table.getColumn("times").getWidth();
      int eventWidth = _table.getColumn(""+dayofweek).getWidth();

      int xPos = currentFirstColWidth;
      for (int i=1; i<dayofweek; i++)
      {
         xPos += _table.getColumn(""+(i)).getWidth();
      }

      Rectangle bounds = new Rectangle(xPos, yPos, eventWidth, eventHeight);
      //System.out.println("bounds: "+bounds);

      Point offset = _scrollPane.getViewport().getViewPosition();
      bounds.x -= offset.x - 1;
      bounds.y -= offset.y - 1;

      return bounds;
   }

   public void addAdjustmentListener(AdjustmentListener l)
   {
      _scrollPane.getVerticalScrollBar().addAdjustmentListener(l);
      if (l instanceof TableColumnModelListener)
         _table.getColumnModel().addColumnModelListener((TableColumnModelListener) l);
   }

   class WeekViewModel extends AbstractTableModel
   {
      private int _numCellsInDay;
      private double _distance;
      private TimeEO[] _times;

      WeekViewModel()
      {
         updateCellRes();
      }

      private void updateCellRes()
      {
         _numCellsInDay = _daySpan.numIntervals(_cellRes);
         _distance = _daySpan.distance(_cellRes);
         _times = new TimeEO[_numCellsInDay];

         Iterator itr = _daySpan.iterator(_cellRes);
         int i=0;
         while (itr.hasNext())
         {
            _times[i++] = (TimeEO) itr.next();
         }
         fireTableStructureChanged();
      }

      public int getRowCount() { return _numCellsInDay; }
      public int getColumnCount() { return WEEKS.length + 1; }

      public String getColumnName(int column)
      {
         if (column == 0)
            return " ";
         TimeSpan span = _weekSpan.add(Calendar.DATE, column-1);
         return COLHEADER_FORMATTER.format(span.startDate());
      }

      public boolean isCellEditable(int nRow, int nCol) { return false; }

      public Object getValueAt(int nRow, int nCol)
      {
         if (nCol > 0)
            return "";
         return _times[nRow].toString();
      }

   }

   public TimeInterval getTimeInterval() { return WEEK_INTERVAL; }
   public JLabel getLabel() { return _label; }

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

   /** Remove a listener.
    *  @see com.holub.ui.Date_selector
    */
    public synchronized void removeActionListener(ActionListener l)
    {
      subscribers = AWTEventMulticaster.remove(subscribers, l);
    }

   /** Notify the listeners of a scroll or select
    */
   private void fireActionEvent( Date date )
   {
      if (subscribers != null)
          subscribers.actionPerformed( new CalActionEvent(this, date, null) );
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
   /* TODO: this code below is duplicated also in DayView:  refactor please. */
   
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

   public String toString()
   {
      return "Week View";
   }

}

