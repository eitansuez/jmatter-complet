package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 1:34:49 PM
 *
 * @author Eitan Suez
 */
public class PasswordRenderer extends JLabel implements AtomicRenderer
{
   public PasswordRenderer()
   {
      setText("**********");
   }
   public void render(AtomicEObject value) {}

   public void passivate() { }
}
