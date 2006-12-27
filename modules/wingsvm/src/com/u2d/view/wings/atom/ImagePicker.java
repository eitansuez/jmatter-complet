package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.ImgEO;
import com.u2d.ui.LocatableIcon;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import org.wings.*;
import org.wings.border.SLineBorder;

/**
 * @author Eitan Suez
 */
public class ImagePicker
      extends SPanel
      implements AtomicEditor
{
   private SLabel _imgLabel = new SLabel();
   private SPanel _pickArea;
   private ImgEO _eo;
   private SPanel _labelPnl = new SPanel(new SFlowLayout(SConstants.LEFT_ALIGN));
   
   public ImagePicker()
   {
      setLayout(new SFlowDownLayout());
      _imgLabel.setBorder(new SLineBorder(Color.black));
      _labelPnl.add(_imgLabel);
      add(_labelPnl);
   }

   public void putInEditMode()
   {
      _labelPnl.add(pickArea());
   }

   public void render(AtomicEObject value)
   {
      _eo = (ImgEO) value;
      _imgLabel.setText(_eo.isEmpty() ? _eo.emptyCaption() : "");
      _imgLabel.setIcon(new SImageIcon(_eo.imageValue()));
   }
   
   public int bind(AtomicEObject value)
   {
      ImgEO eo = (ImgEO) value;
      Image img = ((SImageIcon) _imgLabel.getIcon()).getImage();
      eo.setValue(new ImageIcon(img));
      return 0;
   }

   public void passivate()
   {
      if (_pickArea != null) _labelPnl.remove(_pickArea);
   }

   private SPanel pickArea()
   {
      if (_pickArea != null) return _pickArea;
      //otherwise create it
      _pickArea = new SPanel(new SFlowLayout(SConstants.LEFT_ALIGN));
      final SFileChooser chooser = new SFileChooser();

      _pickArea.add(chooser);
      SButton pickBtn = new SButton("Upload");
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
               
               String path = file.getCanonicalPath();
               LocatableIcon icon = new LocatableIcon(path);
               icon.setImage(_eo.processRawIcon(icon));

               if (icon == _eo.nullIcon() || icon == null)
               {
                  _imgLabel.setText(_eo.emptyCaption());
                  _imgLabel.setIcon(new SImageIcon(_eo.nullIcon()));
               }
               else
               {   
                  _imgLabel.setIcon(new SImageIcon(icon));
                  _imgLabel.setText("");
               }
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
