package com.u2d.view.echo2;

import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.WindowPane;
import com.u2d.ui.desktop.Positioning;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 29, 2006
 * Time: 4:40:29 PM
 */
public class DesktopPane
      extends ContentPane
{
   public DesktopPane()
   {
      super();
      setBackground(new Color(0x806ecf));
   }


   public void addFrame(WindowPane frame)
   {
      frame.setVisible(true);
      add(frame);
   }

   // tbd..
   public void addFrame(WindowPane frame, Positioning positioning)
   {
      addFrame(frame);
   }

   public void positionFrame(WindowPane frame, Positioning positioning)
   {
      // tbd
   }
}
