package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.URI;
import com.u2d.type.atom.ParseException;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:46:49 PM
 *
 * @author Eitan Suez
 */
public class URIEditor extends JTextField implements AtomicEditor
{
   public URIEditor()
   {
      setColumns(20);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      URI eo = (URI) value;
      try
      {
         eo.parseValue(getText());
         return 0;
      }
      catch (ParseException ex)
      {
         eo.fireValidationException(ex.getMessage());
         return 1;
      }
   }

   public void passivate() { }
}
