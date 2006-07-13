/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import com.u2d.type.composite.Folder;
import com.u2d.view.ComplexEView;
import com.u2d.view.ListEView;
import com.u2d.view.swing.list.AlternateListView;

/**
 * @author Eitan Suez
 */
public class FolderView extends FolderPanel implements ComplexEView
{
   // basically an iconlistview of the folder items..
   
   private ListEView _innerView;
   
   public FolderView(Folder folder)
   {
      super(folder);
      
      _innerView =  new AlternateListView(_folder.getItems(), 
            new String[] {"listiconsview", "listview"});  // view as list or icons

      setLayout(new BorderLayout());
      add((JComponent) _innerView, BorderLayout.CENTER);
   }
   
   
   public void detach()
   {
      _innerView.detach();
   }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}
   
}
