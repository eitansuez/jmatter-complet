package com.u2d.view.echo;

import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.WindowPaneListener;
import nextapp.echo.app.event.WindowPaneEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 5, 2008
 * Time: 12:35:58 AM
 */
public abstract class BaseFrame extends WindowPane implements WindowPaneListener
{
   public BaseFrame()
   {
      addWindowPaneListener(this);
   }

   public void windowPaneClosing(WindowPaneEvent e)
   {
      detach();
   }

   public abstract void detach();

}
