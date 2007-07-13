package com.u2d.view.swing.simplecal;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 6, 2005
 * Time: 3:48:23 PM
 */
public interface DropListener extends EventListener
{
   public void itemDropped(CalDropEvent evt);
}
