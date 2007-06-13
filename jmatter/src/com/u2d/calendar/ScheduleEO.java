package com.u2d.calendar;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 7, 2005
 * Time: 9:56:00 AM
 */
public abstract class ScheduleEO extends AbstractComplexEObject
                                 implements Schedulable
{

   protected Schedule _schedule = null;

   public Schedule schedule()
   {
      if (_schedule == null)
         _schedule = new Schedule(this);
      return _schedule;
   }

   @Cmd(mnemonic='s')
   public Schedule SeeSchedule(CommandInfo cmdInfo)
   {
      return schedule();
   }

}
