package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.StringEO;
import com.u2d.view.swing.ActionNotifier;
import com.u2d.element.Field;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 12:51:34 PM
 *
 * @author Eitan Suez
 */
public class StringEditor extends JTextField implements AtomicEditor, ActionNotifier
{
   public StringEditor()
   {
      super(12);
   }
   
   public void render(AtomicEObject value)
   {
      if (value.field() != null && value.field().displaysize() > 0
            && value.field().displaysize() != getColumns())
      {
         setColumns(value.field().displaysize());
      }
      
      StringEO eo = (StringEO) value;
      if (!getText().equals(eo.stringValue()))
         setText(eo.stringValue());
   }
   public int bind(AtomicEObject value)
   {
      StringEO eo = (StringEO) value;
      eo.setValue(getText());
      return 0;
   }

   public void passivate()
   {
      setText("");
   }

}
