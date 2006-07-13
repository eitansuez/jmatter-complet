package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.type.composite.Folder;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeEvent;
import org.wings.SPanel;
import org.wings.SComponent;
import org.wings.SScrollPane;

/**
 * for now flatten the tabs and add all items to a single
 * vertical panel..this is not done yet..
 */
public class OutlookFolderView extends SPanel implements ComplexEView
{
   private Folder _folder;
   private List _tabs = new ArrayList();

   public OutlookFolderView(Folder folder)
   {
      _folder = folder;

      ComplexEObject item;
      for (int i=0; i<folder.size(); i++)
      {
         item = (ComplexEObject) folder.get(i);
         if (item instanceof Folder)
         {
            Folder subfolder = (Folder) item;
//            SPanel pnl = new VerticalFolderPane(subfolder);
//            addTab(subfolder.getName().stringValue(),
//                   folder.iconSm(),
//                   makeScrollPane((SComponent) pnl));
//            _tabs.add(pnl);
         }
      }

   }

   class VerticalFolderPane // extends FolderPanel
                            implements ListDataListener
   {
      VerticalFolderPane(Folder folder)
      {
//         super(folder);
//         setLayout(new PercentLayout(PercentLayout.VERTICAL, 0));
//         setOpaque(false);
         folder.getItems().addListDataListener(this);

         ComplexEObject item;
         for (int i=0; i<folder.size(); i++)
         {
            item = (ComplexEObject) folder.get(i);
//            add((JComponent) item.getIconView());
         }
      }

      public void intervalAdded(final ListDataEvent e) { }
      public void intervalRemoved(final ListDataEvent e) { }
      public void contentsChanged(final ListDataEvent e) {}
   }

   public EObject getEObject() { return _folder; }
   public void detach() { }
   public boolean isMinimized() { return false; }
   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}

}

