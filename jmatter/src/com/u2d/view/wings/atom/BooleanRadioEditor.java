package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.BooleanEO;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.wings.SPanel;
import org.wings.SRadioButton;
import org.wings.SButtonGroup;
import org.wings.SGridLayout;

/**
 * Date: Jun 8, 2005
 * Time: 1:48:45 PM
 *
 * @author Eitan Suez
 */
public class BooleanRadioEditor extends SPanel implements AtomicEditor
{
   private SRadioButton _yesBtn, _noBtn;

   public BooleanRadioEditor()
   {
      _yesBtn = new SRadioButton("Yes");
      _noBtn = new SRadioButton("No");

      _yesBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            _yesBtn.setSelected(true);
         }
      });
      _noBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            _noBtn.setSelected(true);
         }
      });

      SButtonGroup group = new SButtonGroup();
      group.add(_yesBtn);
      group.add(_noBtn);

      setLayout(new SGridLayout(1, 2));
      add(_yesBtn);
      add(_noBtn);
   }

   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      SRadioButton btn = (eo.booleanValue()) ? _yesBtn : _noBtn;
      btn.setSelected(true);
   }

   public int bind(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      eo.setValue(_yesBtn.isSelected());
      return 0;
   }

   public void passivate() { }

}
