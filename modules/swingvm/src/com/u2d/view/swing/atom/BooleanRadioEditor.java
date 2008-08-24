package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.BooleanEO;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;

import net.miginfocom.swing.MigLayout;

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
      this(ComplexType.localeLookupStatic("yes"), ComplexType.localeLookupStatic("no"));
   }
   public BooleanRadioEditor(String yes, String no)
   {
      _yesBtn = new JRadioButton(yes);
      _yesBtn.setOpaque(false);
      _noBtn = new JRadioButton(no);
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

      MigLayout layout = new MigLayout();
      setLayout(layout);
      add(_yesBtn);
      add(_noBtn);
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

   // hack.  basically the callback 'focuslost' calls bind again
   // causing desired change notification.
   public synchronized void addFocusListener(final FocusListener l)
   {
      _yesBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            l.focusLost(null);
         }
      });
      _noBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            l.focusLost(null);
         }
      });
   }

   public void passivate() { }

}
