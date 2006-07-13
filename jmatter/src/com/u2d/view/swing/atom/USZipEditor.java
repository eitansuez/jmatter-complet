package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;

import javax.swing.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:58:59 PM
 *
 * @author Eitan Suez
 */
public class USZipEditor extends JTextField implements AtomicEditor
{
   public USZipEditor()
   {
      setColumns(9);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public int bind(AtomicEObject value)
   {
      try
      {
         value.parseValue(getText());
         return 0;
      }
      catch (java.text.ParseException ex)
      {
         value.fireValidationException(ex.getMessage());
         return 1;
      }
   }
   
   public void passivate() { setText(""); }
   
}
