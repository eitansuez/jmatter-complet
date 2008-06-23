/*
 * Created on Feb 3, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.type.atom.*;
import javax.swing.*;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * @author Eitan Suez
 */
public class FilePicker extends JPanel implements AtomicEditor
{
   private JLabel _fileLabel = new JLabel();
   private JButton _pickBtn;
   private FileEO.FileUseIntent _intent;

   public FilePicker()
   {
      setOpaque(false);
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(_fileLabel);
   }

   public FilePicker putInEditMode()
   {
      add(pickBtn());
      return this;
   }

   public void passivate()
   {
      if (_pickBtn != null)
         remove(_pickBtn);
   }

   public void render(AtomicEObject value)
   {
      FileEO eo = (FileEO) value;
      _intent = eo.getIntent();
      _fileLabel.setText(eo.stringValue());
   }
   
   public int bind(AtomicEObject value)
   {
      FileEO eo = (FileEO) value;
      eo.setValue(new File(_fileLabel.getText()));
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
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            
            int result;
            if (_intent == FileEO.FileUseIntent.OPEN)
            {
               result = chooser.showOpenDialog(FilePicker.this);
            }
            else
            {
               result = chooser.showSaveDialog(FilePicker.this);
            }
            if (result != JFileChooser.APPROVE_OPTION)
               return;
            File file = chooser.getSelectedFile();
            START_PATH = file.getParentFile();
            try
            {
               _fileLabel.setText(file.getCanonicalPath());
               CloseableJInternalFrame.updateSize(FilePicker.this);
            }
            catch (java.io.IOException ex)
            {
               JOptionPane.showInternalMessageDialog(FilePicker.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
         }
      });

      return _pickBtn;
   }

}