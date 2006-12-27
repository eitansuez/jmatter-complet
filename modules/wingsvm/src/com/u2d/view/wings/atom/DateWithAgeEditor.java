package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.DateEO;
import org.wings.SLabel;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 2, 2005
 * Time: 9:52:38 AM
 */
public class DateWithAgeEditor extends DateEditor
{
   private SLabel _ageLabel = new SLabel();

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
