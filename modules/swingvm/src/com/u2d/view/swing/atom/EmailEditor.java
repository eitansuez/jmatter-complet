package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.Email;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:43:52 PM
 *
 * @author Eitan Suez
 */
public class EmailEditor extends JTextField implements AtomicEditor
{
   public EmailEditor()
   {
      setColumns(15);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      ((Email) value).parseValue(getText());
      return 0;
   }

   public void passivate() { }
}
