package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.StringEO;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
import com.u2d.ui.multipick.MultiListPicker;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * This is a component for editing a value that is serialized/marshalled/persisted as a string
 * but that really represents multiple choices (like a set of checkboxes).  Pass it a list of
 * options like new String[] {"one", "two", "three"}
 * And then call setValues("one,two") will "check" the first two options.  The editor, through
 * a dropdown (a picker) lets you alter your selectionset.
 * Then getValues() will return the chosen/selected options as a comma-delimited string again,
 * as in "one,three".
 *
 * @see MultiListPicker
 *
 * @author Eitan Suez
 */
public class MultiPickEditor extends MultiListPicker implements AtomicEditor, ValidationListener
{
   public MultiPickEditor(String[] options)
   {
      super(options);

      setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
   }

   public void render(AtomicEObject value)
   {
      StringEO eo = (StringEO) value;
      setValues(eo.stringValue());
   }
   public int bind(AtomicEObject value)
   {
      StringEO eo = (StringEO) value;
      eo.setValue(getValues());
      return 0;
   }

   public void passivate() { }

   public void validationException(ValidationEvent evt)
   {
      AtomicView.toggleValidationClientProperty(this, evt);
   }

   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      inferSize(g);
      AtomicView.decorateComponentForValidation(g, this, false);
   }

   private boolean inferredSize = false;
   /**
    * let's fix size to be wide enough to display, say, up to 45% of its options.
    * called from paintComponent so can get a reference to Graphics, necessary
    * to obtain font metrics..
    */
   private synchronized void inferSize(Graphics g)
   {
      if (!inferredSize)
      {
         StringBuffer text = new StringBuffer("");
         for (String option : options)
         {
            text.append(option).append(", ");
         }
         int width = SwingUtilities.computeStringWidth(g.getFontMetrics(), text.toString());
         int preferredWidth = (int) (width * 0.45);
         Dimension p = getPreferredSize();
         Dimension size = new Dimension(preferredWidth, p.height-2);
         label.setPreferredSize(size);
         label.setSize(size);
         label.setMaximumSize(size);
         inferredSize = true;
      }
   }

   protected void paintBorder(Graphics g)
   {
      super.paintBorder(g);
      AtomicView.decorateBorderForValidation(g, this);
   }


}