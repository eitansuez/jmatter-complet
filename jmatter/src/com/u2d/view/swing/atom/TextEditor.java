package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;
import com.u2d.type.atom.TextEO;
import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 2:02:47 PM
 *
 * @author Eitan Suez
 */
public class TextEditor extends JPanel implements AtomicRenderer, AtomicEditor
{
   private JTextArea _area;

   public TextEditor()
   {
      _area = new com.u2d.ui.MyTextArea(10, 30);
      _area.setWrapStyleWord(true);
      JScrollPane scrollPane = new JScrollPane(_area);
      setLayout(new BorderLayout());
      add(scrollPane, BorderLayout.CENTER);
   }

   public void setRows(int rows)
   {
      _area.setRows(rows);
   }
   
   public void setEditable(boolean editable)
   {
      _area.setEditable(editable);
   }

   public int bind(AtomicEObject value)
   {
      TextEO eo = (TextEO) value;
      eo.parseValue(_area.getText());
      return 0;
   }

   public void render(AtomicEObject value)
   {
      TextEO eo = (TextEO) value;
      if (eo.isBrief() && _area.getRows() != 2)
      {
         // don't recall:  why am i doing this?  am already invoked in edt.
         // i seem to recall maybe there was a good reason??  ach.
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _area.setRows(2);
            }
         });
      }
      _area.setText(eo.stringValue());
      _area.setLineWrap(eo.wraps());
   }

   public void passivate()
   {
      _area.setText("");
      _area.setEditable(false);
   }
}
