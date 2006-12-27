package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.FileEO;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import org.wings.*;

/**
 * Status:  current implementation incorrect.
 * Would like the convenience of using a filechooser for picking
 * files.  however what i want to submit back to the server is the
 * actual name of the file on the client, not the file contents. hmm..
 * 
 * @author Eitan Suez
 */
public class FilePicker
      extends SPanel
      implements AtomicEditor
{
   private SLabel _fileLabel = new SLabel();
   private SPanel _pickArea;
   private FileEO _eo;

   public FilePicker()
   {
      setLayout(new SFlowLayout(SConstants.LEFT_ALIGN));
      add(_fileLabel);
   }

   public FilePicker putInEditMode()
   {
      add(pickArea());
      return this;
   }

   public void passivate()
   {
      if (_pickArea != null) remove(_pickArea);
   }

   public void render(AtomicEObject value)
   {
      FileEO eo = (FileEO) value;
      _eo = eo;
      _fileLabel.setText(eo.stringValue());
   }
   
   public int bind(AtomicEObject value)
   {
      FileEO eo = (FileEO) value;
      eo.setValue(new File(_fileLabel.getText()));
      return 0;
   }

   private SPanel pickArea()
   {
      if (_pickArea != null) return _pickArea;
      //otherwise create it
      _pickArea = new SPanel(new SFlowLayout(SConstants.LEFT_ALIGN));
      
      final SFileChooser chooser = new SFileChooser();
      _pickArea.add(chooser);
      
      SButton pickBtn = new SButton("Set File");
      _pickArea.add(pickBtn);
      
      pickBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            try
            {
               File file = chooser.getFile();
               if (file == null)
                  return;
            
               _fileLabel.setText(file.getCanonicalPath());
            }
            catch (java.io.IOException ex)
            {
               _eo.fireValidationException(ex.getMessage());
            }
         }
      });

      return _pickArea;
   }

}
