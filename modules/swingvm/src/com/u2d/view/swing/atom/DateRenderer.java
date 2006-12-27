package com.u2d.view.swing.atom;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 3:08:20 PM
 *
 * @author Eitan Suez
 */
public class DateRenderer extends JPanel implements AtomicRenderer
{
   private JLabel _label = new JLabel();
   protected CellConstraints _cc;

   public DateRenderer()
   {
      _label.setOpaque(false);

      FormLayout layout = new FormLayout("pref, 5px, pref", "pref");
      setLayout(layout);
      _cc = new CellConstraints();

      add(_label, _cc.xy(1, 1));
   }

   public void render(AtomicEObject value)
   {
      _label.setText(value.toString());
   }

   public void passivate() { }
}
