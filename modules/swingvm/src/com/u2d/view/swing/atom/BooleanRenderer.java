package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;
import com.u2d.type.atom.BooleanEO;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 1:46:35 PM
 *
 * @author Eitan Suez
 */
public class BooleanRenderer extends JLabel implements AtomicRenderer
{
   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      setText((eo.booleanValue()) ? "Yes" : "No");
   }

   public void passivate() { }
}
