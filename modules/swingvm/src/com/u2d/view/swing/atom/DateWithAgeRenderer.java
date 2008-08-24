/*
 * Created on Feb 10, 2005
 */
package com.u2d.view.swing.atom;

import com.u2d.type.atom.DateEO;
import com.u2d.model.AtomicEObject;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class DateWithAgeRenderer extends DateRenderer
{
   private JLabel _ageLabel = new JLabel();
   {
      _ageLabel.setOpaque(false);
   }

   public DateWithAgeRenderer()
   {
      super();
      add(_ageLabel);
   }

   public void render(AtomicEObject value)
   {
      super.render(value);

      DateEO eo = (DateEO) value;
      _ageLabel.setText(eo.ageString());
   }

}
