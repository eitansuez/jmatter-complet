package com.u2d.view.swing.calendar;

import com.u2d.model.ComplexEObject;
import com.u2d.calendar.Schedulable;

import java.util.Date;
import java.awt.dnd.DropTargetDropEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 6, 2005
 * Time: 3:41:27 PM
 */
public class CalDropEvent extends CalActionEvent
{
   protected ComplexEObject _transferObject;
   protected DropTargetDropEvent _dropTargetDropEvent;

   public CalDropEvent(Object source, Date time, 
                       ComplexEObject transferObject, DropTargetDropEvent sourceEvt)
   {
      super(source, time);
      _transferObject = transferObject;
      _dropTargetDropEvent = sourceEvt;
   }

   public CalDropEvent(Object source, Date time, Schedulable schedulable,
                       ComplexEObject transferObject, DropTargetDropEvent sourceEvt)
   {
      super(source, time, schedulable);
      _transferObject = transferObject;
      _dropTargetDropEvent = sourceEvt;
   }

   public ComplexEObject getTransferObject() { return _transferObject; }
   public DropTargetDropEvent getDropTargetDropEvent() { return _dropTargetDropEvent; }

}
