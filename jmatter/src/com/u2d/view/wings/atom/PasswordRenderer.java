package com.u2d.view.wings.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import org.wings.SLabel;

/**
 * Date: Jun 8, 2005
 * Time: 1:34:49 PM
 *
 * @author Eitan Suez
 */
public class PasswordRenderer extends SLabel implements AtomicRenderer
{
   public PasswordRenderer()
   {
      setText("**********");
   }
   public void render(AtomicEObject value) {}

   public void passivate() { }
}
