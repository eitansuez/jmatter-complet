package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.view.ActionNotifier;
import com.u2d.type.atom.BooleanEO;
import javax.swing.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 17, 2008
 * Time: 10:13:05 AM
 */
public class BooleanCheckboxEditor2 extends JCheckBox implements AtomicEditor, ActionNotifier
{
   public BooleanCheckboxEditor2()
   {
      setOpaque(false);
   }

   private boolean _laidout = false;
   public void render(final AtomicEObject value)
   {
      if (!_laidout)
      {
         addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
               bind(value);
            }
         });
         _laidout = true;
      }
      BooleanEO eo = (BooleanEO) value;
      setText(eo.field().label());
      setSelected(eo.booleanValue());
   }

   public int bind(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      eo.setValue(isSelected());
      return 0;
   }

   public void passivate() { }

}
