package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.BooleanEO;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Date: Jun 8, 2005
 * Time: 1:48:45 PM
 *
 * @author Eitan Suez
 */
public class BooleanRadioEditor extends JPanel implements AtomicEditor
{
   private JRadioButton _yesBtn, _noBtn;

   public BooleanRadioEditor()
   {
      _yesBtn = new JRadioButton("Yes");
      _yesBtn.setOpaque(false);
      _noBtn = new JRadioButton("No");
      _noBtn.setOpaque(false);

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

      ButtonGroup group = new ButtonGroup();
      group.add(_yesBtn);
      group.add(_noBtn);

      FormLayout layout = new FormLayout("pref, 3px, pref", "pref");
      DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
      CellConstraints cc = new CellConstraints();
      builder.add(_yesBtn, cc.xy(1, 1));
      builder.add(_noBtn, cc.xy(3, 1));
   }

   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      JRadioButton btn = (eo.booleanValue()) ? _yesBtn : _noBtn;
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
