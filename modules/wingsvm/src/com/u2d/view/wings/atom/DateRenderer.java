package com.u2d.view.wings.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import org.wings.SLabel;
import org.wings.SPanel;

/**
 * Date: Jun 8, 2005
 * Time: 3:08:20 PM
 *
 * @author Eitan Suez
 */
public class DateRenderer
      extends SPanel
      implements AtomicRenderer
{
   private SLabel _label = new SLabel();

   public DateRenderer()
   {
      add(_label);
   }

   public void render(AtomicEObject value)
   {
      _label.setText(value.toString());
   }

   public void passivate() { }
}
