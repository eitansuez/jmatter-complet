package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.swing.ActionNotifier;

import javax.swing.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Date: Jun 8, 2005
 * Time: 1:48:38 PM
 *
 * @author Eitan Suez
 */
public class BooleanCheckboxEditor extends JCheckBox
      implements ItemListener, AtomicEditor, ActionNotifier
{

   public BooleanCheckboxEditor()
   {
      addItemListener(this);
   }

   public void itemStateChanged(ItemEvent e)
   {
      setText( (isSelected()) ? "Yes" : "No" );
   }

   public int bind(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      eo.setValue(isSelected());
      return 0;
   }

   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      setSelected(eo.booleanValue());
      itemStateChanged(null); // text synch with checkbox
   }

   public void passivate() { }
}
