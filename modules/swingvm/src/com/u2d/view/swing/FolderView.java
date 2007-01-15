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

/**
 * @author Eitan Suez
 */
public class FolderView extends FolderPanel implements ComplexEView
{
   // basically an iconlistview of the folder items..
   
   private AlternateListView _innerView;
   
   public FolderView(Folder folder)
   {
      // problem:  droptargethandler doesn't work because
      // most of view is covered by a jlistview or gridlistview
      // that already has a transfer handler set on it..
      super(folder);
      
      _innerView =  new AlternateListView(_folder.getItems(), 
            new String[] {"listiconsview", "listview"});  // view as list or icons

      setLayout(new BorderLayout());
      add(_innerView, BorderLayout.CENTER);
   }
   
   
   public void detach()
   {
      _innerView.detach();
   }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}
   
}
