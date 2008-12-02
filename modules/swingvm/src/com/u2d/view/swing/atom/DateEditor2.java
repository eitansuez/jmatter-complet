/*
 * Created on Feb 4, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.DateEO;
import com.u2d.view.ActionNotifier;
import javax.swing.*;
import java.net.URL;
import java.awt.event.*;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

/**
 * @author Eitan Suez
 */
public class DateEditor2 extends JPanel
                        implements AtomicEditor, ActionNotifier, CompositeEditor
{
   private JXDatePicker datePicker;

   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL calIconURL = loader.getResource("images/calendar.png");
      UIManager.put("JXDatePicker.arrowIcon", new ImageIcon(calIconURL));
   }

   public DateEditor2()
   {
      MigLayout layout = new MigLayout("insets 0");
      setLayout(layout);
      datePicker = new JXDatePicker();
      add(datePicker);
   }

   public void render(AtomicEObject value)
   {
      if (value.isEmpty())
      {
         datePicker.setDate(null);
      }
      else
      {
         DateEO eo = (DateEO) value;
         datePicker.setDate(eo.dateValue());
      }
   }

   public int bind(AtomicEObject value)
   {
      if (datePicker.isEditValid())
      {
         DateEO eo = (DateEO) value;
         eo.setValue(datePicker.getDate());
         return 0;
      }
      else
      {
         value.fireValidationException("invalid date entry");
         return 1;
      }
   }

   // ===

   public JComponent getEditorComponent() { return datePicker; }

   public void passivate() { }


   // added specifically for integration with table cell editing
   public void addActionListener(ActionListener al)
   {
      datePicker.addActionListener(al);
   }
   public void removeActionListener(ActionListener al)
   {
      datePicker.removeActionListener(al);
   }


}