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
import com.u2d.calendar.DateTimeBounds;
import com.u2d.type.atom.*;
import com.u2d.model.ComplexEObject;

/**
 * @author Eitan Suez
 */
public class WeekView extends BaseTimeIntervalView implements ChangeListener
{
   private static final String[] WEEKS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
   private static DateFormat COLHEADER_FORMATTER= new SimpleDateFormat("EEE, MMM dd");

   private static int COLUMN_WIDTH = 100;
   private static int FIRST_COLUMN_WIDTH = 65;
   private static int WEEKEND_COLUMN_WIDTH = 85;

   private static java.text.SimpleDateFormat LABEL_DATE_FORMATTER =
      new java.text.SimpleDateFormat("MMMM dd yyyy");

   private final TimeSpan _daySpan = new TimeSpan();
   private final TimeSpan _weekSpan = new TimeSpan();

   private DateTimeBounds _datetimeBounds;
   
   public WeekView(TimeSheet timesheet, DateTimeBounds bounds)
   {
      _datetimeBounds = bounds;
      _timesheet = timesheet;
      
      // TODO:  need mutable TimeInterval with ChangeListener
	   // TODO:  need mutable TimeInterval with ChangeListener
	   _datetimeBounds.dayStartTime().addChangeListener(this);
	   _datetimeBounds.weekStartTime().addChangeListener(this);
      _datetimeBounds.position().addChangeListener(this);
      
      adjustSpan();
      updateLabel();

      init();
   }

    public void stateChanged(javax.swing.event.ChangeEvent evt)
    {
       adjustSpan();
       SwingUtilities.invokeLater(new Runnable() {
          public void run()
          {
             updateLabel();

             for (int i=1; i<_model.getColumnCount(); i++)
                _table.getColumn(""+i).setHeaderValue(_model.getColumnName(i));

             Calendar cal = Calendar.getInstance();
             cal.setTime(_datetimeBounds.position().dateValue());
             int column = cal.get(Calendar.DAY_OF_WEEK);
             if (_table.getSelectedColumn() != column)
             {
                _table.setColumnSelectionInterval(column, column);
             }

             repaint();
          }
       });
   }

   private void updateLabel()
   {
      _label.setText("Week of "+LABEL_DATE_FORMATTER.format(_weekSpan.startDate()));
   }

   private void adjustSpan()
   {
      Calendar startOfWeek = Calendar.getInstance();
      startOfWeek.setTime(_datetimeBounds.position().dateValue());

      Calendar weekStartTimeCalendar = _datetimeBounds.weekStartTime().calendarValue();

      // TODO: No way to represent this in an EOType.
      startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	   
      startOfWeek.set(Calendar.HOUR_OF_DAY, weekStartTimeCalendar.get(Calendar.HOUR_OF_DAY));
      startOfWeek.set(Calendar.MINUTE, weekStartTimeCalendar.get(Calendar.MINUTE));
      startOfWeek.set(Calendar.SECOND, weekStartTimeCalendar.get(Calendar.SECOND));

      //System.out.println("beginning of week is "+startOfWeek.getTime());

      TimeEO startHr = new TimeEO(startOfWeek.getTimeInMillis());

      Calendar dayStartTimeCalendar = _datetimeBounds.dayStartTime().calendarValue();
      startHr.set(Calendar.HOUR_OF_DAY, dayStartTimeCalendar.get(Calendar.HOUR_OF_DAY));

      _weekSpan.setValue(new TimeSpan(startOfWeek.getTime(), _datetimeBounds.weekInterval()));
      _daySpan.setValue(new TimeSpan(startHr.dateValue(), _datetimeBounds.dayInterval()));
   }

   public TimeSpan getSpan() { return _weekSpan; }
   public TimeInterval getTimeInterval() { return _datetimeBounds.weekInterval(); }

   

   protected void buildTable()
   {
      _model = new WeekTableModel();
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
            else if (evt.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(evt))
            {
               Calendar cal = getDateTimeForCellCoordinates(0, _table.getSelectedColumn());
               _datetimeBounds.position(cal.getTime());
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
      cal.add(Calendar.MINUTE, rowidx*(int)cellRes().timeInterval().getMilis()/(1000*60));
      return cal;
   }


   protected void setupDropHandler()
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

                  TimeSpan moved = calEvent.timeSpan().position(slot.getTime());
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

   
   
   /* note this is not date/time bounds..  this is actually
      bounds used by layout manager to place calevent in the proper location
      on the screen.  it is a function of the calevent's timespan of course.
    */
   public Rectangle getBounds(CalEvent event)
   {
      TimeSpan span = event.timeSpan();
      
      /*
        start with vertical distance:
          want the distance from 7am on the day to start of event
          produce a time span and get its distance():
       */
      Calendar startOfDayCal = span.startCal();
      startOfDayCal.set(Calendar.HOUR_OF_DAY, _daySpan.startCal().get(Calendar.HOUR_OF_DAY));
      startOfDayCal.set(Calendar.MINUTE, _daySpan.startCal().get(Calendar.MINUTE));
      
      TimeSpan distanceSpan = new TimeSpan(startOfDayCal.getTime(), span.startDate());
      double distance = distanceSpan.distance(cellRes().timeInterval());

      int rowHeight = _table.getRowHeight();
      int yPos = (int) (distance * rowHeight) + _table.getTableHeader().getHeight();
      _log.fine("yPos: "+yPos+"; distance: "+distance);

      int eventHeight = (int) ( ( span.duration().getMilis() * rowHeight ) / cellRes().timeInterval().getMilis() );
      eventHeight = Math.max(eventHeight, rowHeight);

      Calendar cal = span.startCal();
      int dayofweek = cal.get(Calendar.DAY_OF_WEEK);

      int currentFirstColWidth = _table.getColumn("times").getWidth();
      int eventWidth = _table.getColumn(""+dayofweek).getWidth();

      int xPos = currentFirstColWidth;
      for (int i=1; i<dayofweek; i++)
      {
         xPos += _table.getColumn(""+(i)).getWidth();
      }

      Rectangle bounds = new Rectangle(xPos, yPos, eventWidth, eventHeight);
      _log.fine("bounds: "+bounds);

      Point offset = _scrollPane.getViewport().getViewPosition();
      bounds.x -= offset.x - 1;
      bounds.y -= offset.y - 1;

      return bounds;
   }

   class WeekTableModel extends AbstractTableModel implements SpanTableModel
   {
      private int _numCellsInDay;
      private TimeEO[] _times;

      WeekTableModel()
      {
         updateCellRes();
      }

      public void updateCellRes()
      {
         _numCellsInDay = _daySpan.numIntervals(cellRes().timeInterval());
         _times = new TimeEO[_numCellsInDay];

         int i=0;
         for (Iterator itr = _daySpan.iterator(cellRes().timeInterval());
              itr.hasNext();)
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

   public String toString()    { return "Week View"; }

}