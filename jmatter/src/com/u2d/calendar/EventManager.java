package com.u2d.calendar;

import com.u2d.type.atom.TimeSpan;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 20, 2007
 * Time: 1:14:34 PM
 */
public interface EventManager extends EventMaker
{
   public void fetchEvents(TimeSpan span);
}
