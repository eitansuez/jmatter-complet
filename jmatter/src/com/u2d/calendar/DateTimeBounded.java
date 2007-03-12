package com.u2d.calendar;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 12, 2007
 * Time: 5:30:58 PM
 */
public interface DateTimeBounded
{
   public DateTimeBounds bounds();
   public void bounds(DateTimeBounds bounds);
}
