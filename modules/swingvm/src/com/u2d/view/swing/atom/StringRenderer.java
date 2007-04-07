package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 12:55:35 PM
 *
 * @author Eitan Suez
 */
public class StringRenderer extends JLabel implements AtomicRenderer
{
   public void render(AtomicEObject value)
   {
      if (value.isEmpty())
         setText("--");
      else
         setText(value.toString());
   }
   public void passivate() {}
}
