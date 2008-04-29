package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.ChoiceEO;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 29, 2008
 * Time: 10:31:28 AM
 */
public class ChoiceRadioEditor extends JPanel implements AtomicEditor
{
   private boolean _laidout = false;
   private JRadioButton[] _buttons;

   public ChoiceRadioEditor() {}

   public void render(AtomicEObject value)
   {
      ChoiceEO eo = (ChoiceEO) value;
      if (! _laidout )
      {
         FormLayout layout = new FormLayout("", "p");
         int numCols = 2 * (eo.entries().size()-1) + 1;
         for (int i=0; i<numCols; i++)
         {
            String descr = ((i%2) == 0) ? "p" : "3dlu";
            layout.appendColumn(new ColumnSpec(descr));
         }
         setLayout(layout);
         DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
         CellConstraints cc = new CellConstraints();

         ButtonGroup group = new ButtonGroup();
         _buttons = new JRadioButton[eo.entries().size()];
         int i=0;
         for (Object entry : eo.entries())
         {
            JRadioButton btn = new JRadioButton((String) entry);
            group.add(btn);
            int col = (i*2)+1;
            builder.add(btn, cc.rc(1, col));
            _buttons[i++] = btn;
         }
         _laidout = true;
      }

      int i=0;
      for (Object entry : eo.entries())
      {
         String code = (String) entry;
         if (code.equals(eo.code()))
         {
            _buttons[i].setSelected(true);
            break;
         }
         i++;
      }
   }

   public int bind(AtomicEObject value)
   {
      ChoiceEO eo = (ChoiceEO) value;
      for (JRadioButton btn : _buttons)
      {
         if (btn.isSelected())
         {
            eo.setValue(btn.getText());
            break;
         }
      }
      return 0;
   }

   public void passivate() { }
}
