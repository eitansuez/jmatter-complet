package com.u2d.view.wings.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.BooleanEO;
import org.wings.SLabel;

/**
 * Date: Jun 8, 2005
 * Time: 1:46:35 PM
 *
 * @author Eitan Suez
 */
public class BooleanRenderer extends SLabel implements AtomicRenderer
{
   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      setText((eo.booleanValue()) ? "Yes" : "No");
   }

   public void passivate() { }
}
