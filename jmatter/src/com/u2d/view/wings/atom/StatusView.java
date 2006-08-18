package com.u2d.view.wings.atom;

import com.u2d.view.AtomicEView;
import com.u2d.view.wings.FieldCaption;
import com.u2d.model.AtomicEObject;
import com.u2d.model.EObject;
import javax.swing.event.ChangeEvent;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SFont;

/**
 * Date: May 17, 2005
 * Time: 9:49:56 AM
 *
 * @author Eitan Suez
 */
public class StatusView extends SPanel implements AtomicEView
{
   AtomicEObject _eo;
   SLabel _label = new SLabel();

   public StatusView(AtomicEObject eo)
   {
      _eo = eo;
      _eo.addChangeListener(this);

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
      _label.setText(_eo.toString());
   }

   private static SFont SMALL_FONT;
   static
   {
      SMALL_FONT = new SFont();
      SMALL_FONT.setSize(10);
   }

}
