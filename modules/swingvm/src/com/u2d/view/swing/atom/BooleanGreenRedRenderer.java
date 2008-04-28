package com.u2d.view.swing.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.BooleanEO;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 15, 2008
 * Time: 4:01:52 PM
 */
public class BooleanGreenRedRenderer extends JLabel implements AtomicRenderer
{
   String greenCaption, redCaption;
   
   public BooleanGreenRedRenderer(String greenCaption, String redCaption)
   {
      this.greenCaption = greenCaption;
      this.redCaption = redCaption;
      setOpaque(true);
      setHorizontalAlignment(SwingConstants.CENTER);
   }
   
   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      if (eo.booleanValue())
      {
         setText(greenCaption);
         setBackground(Color.green);
      }
      else
      {
         setText(redCaption);
         setBackground(Color.red);
      }
   }

   public Dimension getPreferredSize()
   {
      return new Dimension(200, super.getPreferredSize().height);
   }

   public void passivate() { }
}
