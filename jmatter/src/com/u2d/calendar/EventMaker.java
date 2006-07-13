package com.u2d.calendar;

import com.u2d.type.atom.TimeSpan;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 18, 2006
 * Time: 2:53:53 PM
 */
public interface EventMaker
{
   public CalEvent newEvent(TimeSpan span);
}
