package com.u2d.view.wings.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.TextEO;
import org.wings.STextArea;

/**
 * Date: Jun 8, 2005
 * Time: 2:02:47 PM
 *
 * @author Eitan Suez
 */
public class TextEditor extends STextArea implements AtomicRenderer, AtomicEditor
{
   public TextEditor()
   {
      super(10, 30);
   }

   public int bind(AtomicEObject value)
   {
      TextEO eo = (TextEO) value;
      eo.parseValue(getText());
      return 0;
   }

   public void render(AtomicEObject value)
   {
      TextEO eo = (TextEO) value;
      if (eo.isBrief() && getRows() != 2)
      {
         // don't recall:  why am i doing this?  am already invoked in edt.
         // i seem to recall maybe there was a good reason??  ach.
         setRows(2);
      }
      setText(eo.stringValue());
   }

   public void passivate()
   {
      setText("");
      setEditable(false);
   }
}
