package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * Date: Jun 8, 2005
 * Time: 3:08:20 PM
 *
 * @author Eitan Suez
 */
public class DateRenderer extends JPanel implements AtomicRenderer
{
   private JLabel _label = new JLabel();

   public DateRenderer()
   {
      setOpaque(false);
      _label.setOpaque(false);

      MigLayout layout = new MigLayout();
      setLayout(layout);

      add(_label);
   }

   public void render(AtomicEObject value)
   {
      _label.setText(value.toString());
   }

   public void passivate() { }
}
