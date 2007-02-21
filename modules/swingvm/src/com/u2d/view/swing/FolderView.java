/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import com.u2d.type.composite.Folder;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.GridListView;
import com.u2d.model.EObject;

/**
 * @author Eitan Suez
 * 
 * basically an iconlistview of the folder items..
 */
public class FolderView extends JPanel
      implements ComplexEView
{
   protected Folder _folder;
   private GridListView _iconListView;
   
   public FolderView(Folder folder)
   {
      _folder = folder;
      setOpaque(true);
      setBackground(Color.white);
      setLayout(new BorderLayout());
      _iconListView = new GridListView(_folder.getItems());
      add(_iconListView, BorderLayout.CENTER);
   }
   
   public EObject getEObject() { return _folder; }
   public boolean isMinimized() { return false; }
   public void detach() { _iconListView.detach(); }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}
   
}
