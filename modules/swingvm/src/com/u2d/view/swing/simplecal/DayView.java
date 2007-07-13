/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.simplecal;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.u2d.calendar.*;
import com.u2d.type.atom.*;
import com.u2d.model.ComplexEObject;

/**
 * @author Eitan Suez
 */
public class DayView extends BaseTimeIntervalView implements ChangeListener
{
   private static int COLUMN_WIDTH = 300;
   private static int FIRST_COLUMN_WIDTH = 65;

   public static TimeInterval INTERVAL = new TimeInterval(Calendar.HOUR, 24);
   
   private static java.text.SimpleDateFormat LABEL_DATE_FORMATTER =
      new java.text.SimpleDateFormat("EEEE MMMM dd yyyy");

   private final TimeSpan _daySpan = new TimeSpan();
   private DateTimeBounds _datetimeBounds;

   public DayView(DateTimeBounds bounds)
   {
      _datetimeBounds = bounds;
      // TODO:  need mutable TimeInterval with ChangeListener
      _datetimeBounds.dayStartTime().addChangeListener(this);
      _datetimeBounds.position().addChangeListener(this);

      adjustSpan();
      updateLabel();

      init();

      TableColumn column = new TableColumn(1, COLUMN_WIDTH,
                                           new DefaultTableCellRenderer(), null);
      _table.addColumn(column);
   }

    public void stateChanged(javax.swing.event.ChangeEvent evt)
    {
       adjustSpan();
       SwingUtilities.invokeLater(new Runnable() {
          public void run()
          {
             updateLabel();
             repaint();
          }
       });
   }

   private void updateLabel()
   {
      _label.setText(LABEL_DATE_FORMATTER.format(_datetimeBounds.position().dateValue()));
   }

   private void adjustSpan()
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(_datetimeBounds.position().dateValue());

      Calendar dayStartTimeCalendar = _datetimeBounds.dayStartTime().calendarValue();

      cal.set(Calendar.HOUR_OF_DAY, dayStartTimeCalendar.get(Calendar.HOUR_OF_DAY));
      cal.set(Calendar.MINUTE, dayStartTimeCalendar.get(Calendar.MINUTE));
      cal.set(Calendar.SECOND, dayStartTimeCalendar.get(Calendar.SECOND));
      _daySpan.setValue(new TimeSpan(cal.getTime(), _datetimeBounds.dayInterval()));
   }

   public TimeSpan getSpan() { return _daySpan; }
   public TimeInterval getTimeInterval() { return INTERVAL; }



   protected void buildTable()
   {
      _model = new DayTableModel();
      _table = new JTable();

      _table.setAutoCreateColumnsFromModel(false);
      _table.setModel(_model);

      TableColumn column = new TableColumn(0, FIRST_COLUMN_WIDTH,
                                           new RowHeaderCellRenderer(), null);
      column.setMinWidth(FIRST_COLUMN_WIDTH);
      column.setMaxWidth(FIRST_COLUMN_WIDTH);
      column.setIdentifier("times");
      _table.addColumn(column);

      _table.setGridColor(Color.lightGray);
      _table.setShowGrid(true);
      _table.setRowSelectionAllowed(false);
      _table.setColumnSelectionAllowed(true);
      _table.getTableHeader().setReorderingAllowed(true);
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

               fireActionEvent(getSelectedTime());
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
               int rowIndex = _table.rowAtPoint(location);
               Date timeSlot = getSelectedTime(rowIndex);

//               TableColumnModel tcmodel = _table.getColumnModel();
//               int colIndex = _table.columnAtPoint(location);
//               TableColumn column = tcmodel.getColumn(colIndex);

               if (transferObject instanceof CalEvent)
               {
                  final CalEvent calEvent = (CalEvent) transferObject;

                  TimeSpan moved = calEvent.timeSpan().position(timeSlot);
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
                  fireDropEvent(new CalDropEvent(this, timeSlot,  
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

   private Date getSelectedTime()
   {
      return getSelectedTime(_table.getSelectedRow());
   }

   // translate cell position into start day and time
   private Date getSelectedTime(int rowidx)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(_daySpan.startDate());
      cal.add(Calendar.MINUTE, rowidx*(int)_cellRes.timeInterval().getMilis()/(1000*60));
      return cal.getTime();
   }

   /* note this is not date/time bounds..  this is actually
      bounds used by layout manager to place calevent in the proper location
      on the screen.  it is a function of the calevent's timespan of course.
    */
   public Rectangle getBounds(CalEvent event)
   {
      TimeSpan span = event.timeSpan();

      Calendar startOfDayCal = span.startCal();
      startOfDayCal.set(Calendar.HOUR_OF_DAY, _daySpan.startCal().get(Calendar.HOUR_OF_DAY));
      startOfDayCal.set(Calendar.MINUTE, _daySpan.startCal().get(Calendar.MINUTE));
      
      TimeSpan distanceSpan = new TimeSpan(startOfDayCal.getTime(), span.startDate());
      double distance = distanceSpan.distance(_cellRes.timeInterval());

      int rowHeight = _table.getRowHeight();
      int yPos = (int) (distance * rowHeight) + _table.getTableHeader().getHeight();
      _log.fine("yPos: "+yPos+"; distance: "+distance);

      int eventHeight = (int) ( ( span.duration().getMilis() * rowHeight ) / _cellRes.timeInterval().getMilis() );
      eventHeight = Math.max(eventHeight, rowHeight);

      int xPos = _table.getColumn("times").getWidth();
      TableColumnModel tcmodel = _table.getColumnModel();
      int i=1;

      if ( (i+1) > _table.getColumnCount() )
         return new Rectangle(0, 0, 0, 0);

      TableColumn column = tcmodel.getColumn(i++);
      int eventWidth = column.getWidth();

      Rectangle bounds = new Rectangle(xPos, yPos, eventWidth, eventHeight);

      Point offset = _scrollPane.getViewport().getViewPosition();
      bounds.x -= offset.x - 1;
      bounds.y -= offset.y - 1;

      return bounds;
   }


   class DayTableModel extends AbstractTableModel implements SpanTableModel
   {
      private int _numCellsInDay;
      private TimeEO[] _times;

      DayTableModel()
      {
         updateCellRes();
      }

      public void updateCellRes()
      {
         _numCellsInDay = _daySpan.numIntervals(_cellRes.timeInterval());
         _times = new TimeEO[_numCellsInDay];

         int i=0;
         for (Iterator itr = _daySpan.iterator(_cellRes.timeInterval());
              itr.hasNext();)
         {
            _times[i++] = (TimeEO) itr.next();
         }
         fireTableStructureChanged();
      }

      public int getRowCount() { return _numCellsInDay; }
      public int getColumnCount() { return 2; }

      public String getColumnName(int column)
      {
         if (column == 0)
            return " ";
         return ""; // TODO: Set an appropriate caption
      }

      public boolean isCellEditable(int nRow, int nCol) { return false; }

      public Object getValueAt(int nRow, int nCol)
      {
         if (nCol > 0)
            return "";
         return _times[nRow].toString();
      }

   }

   public String toString() { return "Day View"; }
}

