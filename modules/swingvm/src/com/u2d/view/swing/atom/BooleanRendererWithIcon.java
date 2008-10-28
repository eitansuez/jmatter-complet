package com.u2d.view.swing.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.BooleanEO;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 15, 2008
 * Time: 4:01:52 PM
 *
 * The idea here is for the two states to be represented by two icons.
 * e.g. connected/disconnected with --- and -X-
 * pass in icon/caption pair for true/false in the constructor
 */
public class BooleanRendererWithIcon extends JLabel implements AtomicRenderer
{
   Icon trueIcon, falseIcon;
   String trueCaption, falseCaption;

   public BooleanRendererWithIcon(String trueCaption, Icon trueIcon, String falseCaption, Icon falseIcon)
   {
      this.trueCaption = trueCaption;
      this.trueIcon = trueIcon;
      this.falseCaption = falseCaption;
      this.falseIcon = falseIcon;
      setOpaque(false);
      setHorizontalAlignment(SwingConstants.CENTER);
   }

   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      String caption = (eo.booleanValue()) ? trueCaption : falseCaption;
      Icon icon = (eo.booleanValue()) ? trueIcon : falseIcon;
      setText(caption);
      setIcon(icon);
   }

   public void passivate() { }
}