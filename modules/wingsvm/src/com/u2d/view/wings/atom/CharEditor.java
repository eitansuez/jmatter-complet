package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.CharEO;
import org.wings.STextField;

/**
 * Date: Jun 8, 2005
 * Time: 2:12:34 PM
 *
 * @author Eitan Suez
 */
public class CharEditor extends STextField implements AtomicEditor
{
   public CharEditor()
   {
      super();
      setColumns(1);
   }

   public void render(AtomicEObject value)
   {
      CharEO eo = (CharEO) value;
      setText(eo.stringValue());
   }

   public int bind(AtomicEObject value)
   {
      CharEO eo = (CharEO) value;
      eo.parseValue(getText());
      return 0;
   }

   public void passivate() { }
}
