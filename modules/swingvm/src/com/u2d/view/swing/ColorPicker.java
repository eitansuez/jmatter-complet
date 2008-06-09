package com.u2d.view.swing;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 4, 2006
 * Time: 9:13:42 PM
 * @author Eitan Suez
 */
public class ColorPicker extends JPanel implements AtomicEditor
{
   private JLabel _colorLabel = new ColorLabel();
   private JButton _pickBtn;
   private ColorEO _eo;
   
   public ColorPicker()
   {
      this(true);
   }
   
   public ColorPicker(boolean editMode)
   {
      setOpaque(false);
      add(_colorLabel);
      if (editMode)
      {
         add(pickBtn());
      }
   }

   public void passivate()
   {
      if (_pickBtn != null)
         remove(_pickBtn);
   }

   public void render(AtomicEObject value)
   {
      _eo = (ColorEO) value;
      _colorLabel.setBackground(_eo.colorValue());
   }
   
   public int bind(AtomicEObject value)
   {
      ColorEO eo = (ColorEO) value;
      eo.setValue(_colorLabel.getBackground());
      return 0;
   }

   private JButton pickBtn()
   {
      if (_pickBtn != null) return _pickBtn;
      //otherwise create it
      _pickBtn = new JButton("...");
      _pickBtn.setOpaque(false);
      
      _pickBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            Color selectedColor = 
                  JColorChooser.showDialog(_pickBtn, "Select a color", _eo.colorValue());
            if (selectedColor != null)
            {
               _colorLabel.setBackground(selectedColor);
            }
         }
      });

      return _pickBtn;
   }

   
   static class ColorLabel extends JLabel
   {
      ColorLabel()
      {
         setBorder(BorderFactory.createLineBorder(Color.black));
         setOpaque(true);
      }
      
      private static Dimension dim = new Dimension(16,16);
      public Dimension getPreferredSize() { return dim; }
   }
   
}