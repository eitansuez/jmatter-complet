/*
 * Created on Sep 17, 2003
 */
package com.u2d.view.swing.calendar.simple;

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
import com.u2d.view.swing.calendar.RowHeaderCellRenderer;
import com.u2d.view.swing.calendar.CalDropEvent;
import com.u2d.view.swing.calendar.BaseWeekView;

/**
 * @author Eitan Suez
 */
public class WeekView extends BaseWeekView
{

   public WeekView(TimeSheet timesheet, DateTimeBounds bounds)
   {
      super(timesheet, bounds);
   }

   protected void fireDoubleClickEvent(Calendar cal)
   {
      fireActionEvent(cal.getTime());
   }

}