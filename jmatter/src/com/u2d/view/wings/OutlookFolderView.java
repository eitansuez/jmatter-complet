package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.type.composite.Folder;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.app.Tracing;

import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.wings.*;

/**
 * for now flatten the tabs and add all items to a single
 * vertical panel..this is not done yet..
 */
public class OutlookFolderView extends SPanel implements ComplexEView
{
   private Folder _folder;
   private Logger _tracer = Tracing.tracer();

   public OutlookFolderView(Folder folder)
   {
      _folder = folder;
      
      setLayout(new SFlowDownLayout());
      
      _tracer.fine("In outlookfolderview..");
      
      ComplexEObject item;
      for (int i=0; i<folder.size(); i++)
      {
         item = (ComplexEObject) folder.get(i);
         _tracer.fine("processing folder "+item);
         if (item instanceof Folder)
         {
            Folder subfolder = (Folder) item;
            SPanel pnl = new VerticalFolderPane(subfolder);
            _tracer.fine("adding panel..");
            add(pnl);
         }
      }

   }

   class VerticalFolderPane extends SPanel
                            implements ListDataListener
   {
      VerticalFolderPane(Folder folder)
      {
         setLayout(new SFlowDownLayout());
         folder.getItems().addListDataListener(this);

         _tracer.fine("ok.  in verticalfolderpane..");
         
         ComplexEObject item;
         for (int i=0; i<folder.size(); i++)
         {
            item = (ComplexEObject) folder.get(i);
            _tracer.fine("adding item "+item);
            SComponent view = (SComponent) item.getIconView();
            _tracer.fine("view is: "+view);
            add(view);
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
