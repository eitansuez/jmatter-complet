package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.DateEO;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 2, 2005
 * Time: 9:52:38 AM
 */
public class DateWithAgeEditor extends DateEditor
{
   private JLabel _ageLabel = new JLabel();
   {
      _ageLabel.setOpaque(false);
   }

   public DateWithAgeEditor()
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
