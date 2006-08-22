package com.u2d.view;

import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 17, 2006
 * Time: 11:08:58 AM
 */
public interface ActionNotifier
{
   public void addActionListener(ActionListener al);
   public void removeActionListener(ActionListener al);
}
