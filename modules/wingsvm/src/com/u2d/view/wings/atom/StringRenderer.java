package com.u2d.view.wings.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import org.wings.SLabel;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 17, 2006
 * Time: 3:20:27 PM
 */
public class StringRenderer extends SLabel implements AtomicRenderer
{
   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }
   public void passivate() {}
}
