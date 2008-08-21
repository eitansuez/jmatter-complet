package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 21, 2008
 * Time: 12:53:00 PM
 */
public class LTRCapableJSplitPane extends JSplitPane
{

   public void setLeftComponent(Component comp)
   {
      if (getComponentOrientation().isLeftToRight())
      {
         super.setLeftComponent(comp);
      }
      else
      {
         super.setRightComponent(comp);
      }
   }

   public void setRightComponent(Component comp)
   {
      if (getComponentOrientation().isLeftToRight())
      {
         super.setRightComponent(comp);
      }
      else
      {
         super.setLeftComponent(comp);
      }
   }

}
