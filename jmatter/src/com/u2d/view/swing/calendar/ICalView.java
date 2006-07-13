/*
 * Created on Apr 15, 2004
 */
package com.u2d.view.swing.calendar;

import com.u2d.type.atom.TimeInterval;

/**
 * @author Eitan Suez
 */
public interface ICalView
{
   public void shift(boolean forward);
   public void setCellResolution(TimeInterval ti);
}
