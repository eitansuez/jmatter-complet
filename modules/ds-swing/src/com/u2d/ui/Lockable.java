package com.u2d.ui;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 9, 2008
 * Time: 2:48:11 PM
 */
public interface Lockable
{
   public void setLocked(boolean locked);
   public String lockTooltip();
}
