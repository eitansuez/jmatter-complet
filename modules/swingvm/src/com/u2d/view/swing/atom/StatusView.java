package com.u2d.view.swing.atom;

import com.u2d.view.AtomicEView;
import com.u2d.view.swing.FieldCaption;
import com.u2d.model.EObject;
import com.u2d.model.AtomicEObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * Date: May 17, 2005
 * Time: 9:49:56 AM
 *
 * @author Eitan Suez
 */
public class StatusView extends JPanel implements AtomicEView
{
   AtomicEObject _eo;
   JLabel _label = new JLabel();

   public StatusView(AtomicEObject eo)
   {
      _eo = eo;
      _eo.addChangeListener(this);

      setOpaque(false);

      _label.setFont(SMALL_FONT);
      _label.setText(_eo.toString());
      FieldCaption caption = new FieldCaption(_eo.field(), _label);
      caption.setFont(SMALL_FONT);
      add(caption);
      add(_label);
   }

   public EObject getEObject() { return _eo; }
   public void detach() { _eo.removeChangeListener(this); }

   public void stateChanged(ChangeEvent e)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _label.setText(_eo.toString());
         }
      });
   }

   private static Font SMALL_FONT;
   static
   {
      SMALL_FONT = UIManager.getFont("Label.font").deriveFont(10.0f);
   }

}
