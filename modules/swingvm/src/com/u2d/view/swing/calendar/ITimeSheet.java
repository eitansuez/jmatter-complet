package com.u2d.view.swing.calendar;

import com.u2d.calendar.CellResChoice;

import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 16, 2007
 * Time: 4:53:14 PM
 */
public interface ITimeSheet
{
   public void shift(boolean forward);
   public CellResChoice getCellResolution();
   public void setCellResolution(CellResChoice choice);
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
