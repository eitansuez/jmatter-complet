/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.simplecal;

import java.awt.event.ActionEvent;
import java.util.Date;

/**
 * @author Eitan Suez
 */
public class CalActionEvent extends ActionEvent
{
   protected Date _time;
   
   public CalActionEvent(Object source, Date time)
   {
      super(source, ActionEvent.ACTION_PERFORMED, "");
      _time = time;
   }
   
   public Date getTime() { return _time; }
   
}
