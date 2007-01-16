/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import com.u2d.type.composite.Folder;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.AlternateListView;
import com.u2d.view.swing.list.GridListView;

/**
 * @author Eitan Suez
 */
public class FolderView extends FolderPanel implements ComplexEView
{
   // basically an iconlistview of the folder items..
   
   private GridListView _iconListView;
   
   public FolderView(Folder folder)
   {
      super(folder);
      
      setLayout(new BorderLayout());
      _iconListView = new GridListView(_folder.getItems());
      setupToAddItemOnDrop(_iconListView);
      add(_iconListView, BorderLayout.CENTER);
   }
   
   public void detach()
   {
      _iconListView.detach();
   }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}
   
}
