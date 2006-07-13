/*
 * Created on Mar 4, 2004
 */
package com.u2d.view.swing.atom;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.*;

/**
 * A composite view of a boolean that also includes a large body of read-only
 * text, such as used when displaying terms for a user to agree to.
 * 
 * @author Eitan Suez
 */
public class TermsEditor extends JPanel implements AtomicEditor
{
   private BooleanCheckboxEditor _iagree;
   private JTextArea _area;

   public TermsEditor()
   {
      setLayout(new BorderLayout());
      
      _area = new com.u2d.ui.MyTextArea();
      decorateArea(_area);
      
      _area.setEditable(false);
      add(_area, BorderLayout.CENTER);
      
      JLabel label = new JLabel("I Agree to the above terms: ");
      _iagree = new BooleanCheckboxEditor();
      label.setLabelFor((Component) _iagree);
      
      JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
      bottomPnl.add(label);
      bottomPnl.add(_iagree);
      add(bottomPnl, BorderLayout.SOUTH);
   }
   
   private void decorateArea(JTextArea area)
   {
      Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
      Border border = BorderFactory.createTitledBorder("Terms and Conditions");
      border = BorderFactory.createCompoundBorder(margin, border);
      border = BorderFactory.createCompoundBorder(border, margin);
      area.setBorder(border);
   }


   public void render(AtomicEObject value)
   {
      TermsEO eo = (TermsEO) value;
      _area.setText(eo.terms());
      _iagree.bind(eo);  // TermsEO extends BooleanEO
   }

   public int bind(AtomicEObject value)
   {
      _iagree.bind(value);  // forward request to booleancheckboxeditor
      return 0;
   }

   public void passivate() { }

}
