package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 3:08:20 PM
 *
 * @author Eitan Suez
 */
public class DateRenderer extends JLabel implements AtomicRenderer
{
   public DateRenderer()
   {
      setOpaque(false);
      setHorizontalAlignment(JLabel.RIGHT);  // (not trailing)
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public void passivate() { }
}
