/*
 * Created on Feb 3, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import com.u2d.ui.*;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.l2fprod.common.swing.PercentLayout;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * @author Eitan Suez
 */
public class ImagePicker extends JPanel implements AtomicEditor
{
   private JLabel _imgLabel = new JLabel();
   private JButton _pickBtn;
   private ImgEO _eo;
   private JSlider _slider = new MySlider();
   private JPanel _labelPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
   private boolean _editmode;
   
   public ImagePicker()
   {
      this(true);
   }
   
   public ImagePicker(boolean editMode)
   {
      setOpaque(false);
      setLayout(new PercentLayout(PercentLayout.VERTICAL, 1));
      _labelPnl.setOpaque(false);
      _imgLabel.setBorder(BorderFactory.createLineBorder(Color.black));
      _labelPnl.add(_imgLabel);
      add(_labelPnl);
      add(_slider);
      _editmode = editMode;
      if (editMode)
      {
         _labelPnl.add(pickBtn());
      }
      _slider.setVisible(false);
   }

   class MySlider extends JSlider
   {
      public MySlider()
      {
         super(HORIZONTAL, 20, 100, 100);
         setOpaque(false);
         addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               ImageIcon icon = _eo.imageValue();
           
               int width = icon.getIconWidth() * getValue() / 100;
               int height = icon.getIconHeight() * getValue() / 100;
            
               Image scaledImage = icon.getImage().
                     getScaledInstance(width, height, Image.SCALE_FAST);
            
               _imgLabel.setIcon(new ImageIcon(scaledImage));
            }
         });
      }

      public int getWidth()
      {
         if (_eo != null)
         {
            return _eo.imageValue().getIconWidth();
         }
         return super.getWidth();
      }
   }
   
   public void passivate()
   {
      if (_pickBtn != null)
         _labelPnl.remove(_pickBtn);
   }

   public void render(AtomicEObject value)
   {
      _eo = (ImgEO) value;
      _imgLabel.setText(_eo.isEmpty() ? _eo.emptyCaption() : "");
      _imgLabel.setIcon(_eo.imageValue());
      _slider.setVisible(!_editmode && !_eo.isEmpty());
   }
   
   public int bind(AtomicEObject value)
   {
      ImgEO eo = (ImgEO) value;
      eo.setValue((ImageIcon) _imgLabel.getIcon());
      return 0;
   }

   private static File START_PATH = new File(System.getProperty("user.home"));
   
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
            JFileChooser chooser = new JFileChooser(START_PATH);
            int result = chooser.showOpenDialog(ImagePicker.this);
            if (result != JFileChooser.APPROVE_OPTION)
               return;
            java.io.File file = chooser.getSelectedFile();
            START_PATH = file.getParentFile();
            try
            {
               String path = file.getCanonicalPath();
               LocatableIcon icon = new LocatableIcon(path);
               icon.setImage(_eo.processRawIcon(icon));

               if (icon == _eo.nullIcon() || icon == null)
               {
                  _imgLabel.setText(_eo.emptyCaption());
                  _imgLabel.setIcon(_eo.nullIcon());
               }
               else
               {   
                  _imgLabel.setIcon(icon);
                  _imgLabel.setText("");
               }
               CloseableJInternalFrame.updateSize(ImagePicker.this);
            }
            catch (java.io.IOException ex)
            {
               JOptionPane.showInternalMessageDialog(ImagePicker.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
         }
      });

      return _pickBtn;
   }

}