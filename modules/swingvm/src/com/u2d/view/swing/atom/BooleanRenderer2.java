package com.u2d.view.swing.atom;

import javax.swing.*;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.ComplexType;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.ActionNotifier;

/**
 * Date: Jun 8, 2005
 * Time: 1:46:35 PM
 *
 * @author Eitan Suez
 */
public class BooleanRenderer2 extends JCheckBox implements AtomicRenderer
{
   public BooleanRenderer2()
   {
      setOpaque(false);
      setEnabled(false);
   }
   
   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      setText(eo.field().label());
      setSelected(eo.booleanValue());
   }

   public void passivate() { }
}