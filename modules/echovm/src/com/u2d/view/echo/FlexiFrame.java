package com.u2d.view.echo;

import nextapp.echo.app.WindowPane;
import nextapp.echo.app.Component;
import com.u2d.view.EView;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 5, 2008
 * Time: 11:44:15 AM
 */
public class FlexiFrame extends WindowPane
{
   public FlexiFrame(EView view)
   {
      add((Component) view);
   }
   public FlexiFrame(Component comp)
   {
      add(comp);
   }
}
