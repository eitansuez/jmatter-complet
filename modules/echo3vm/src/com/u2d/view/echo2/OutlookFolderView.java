package com.u2d.view.echo2;

import nextapp.echo.extras.app.AccordionPane;
import nextapp.echo.extras.app.layout.AccordionPaneLayoutData;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.type.composite.Folder;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 29, 2006
 * Time: 4:50:31 PM
 */
public class OutlookFolderView extends AccordionPane implements ComplexEView
{
   private Folder _folder;
   private List<VerticalFolderPane> _tabs = new ArrayList<VerticalFolderPane>();

   public OutlookFolderView() {}
   public OutlookFolderView(Folder folder)
   {
      this();
      bind(folder);
   }

   public void bind(Folder folder)
   {
      _folder = folder;

      ComplexEObject item;
      for (int i=0; i<_folder.size(); i++)
      {
         item = (ComplexEObject) _folder.get(i);
         if (item instanceof Folder)
         {
            Folder subfolder = (Folder) item;
            VerticalFolderPane pnl = new VerticalFolderPane(subfolder);
            AccordionPaneLayoutData layout = new AccordionPaneLayoutData();
            layout.setTitle(subfolder.getName().stringValue());
            pnl.setLayoutData(layout);
            add(pnl);
//            addTab(subfolder.getName().stringValue(), _folder.iconSm(), pnl);
            _tabs.add(pnl);
         }
      }

//      setDropTarget(new DropTarget()
//      {
//         public synchronized void drop(DropTargetDropEvent dtde)
//         {
//            VerticalFolderPane vfp = _tabs.get(getSelectedIndex());
//            vfp.getDropTarget().drop(dtde);
//         }
//      });
   }

   public void detach()
   {
      for (VerticalFolderPane v : _tabs)
      {
         v.detach();
      }
      _tabs.clear();
      removeAll();
   }


   class VerticalFolderPane extends Column
         implements ListDataListener
   {
      Folder _folder;
      
      VerticalFolderPane(Folder folder)
      {
         _folder = folder;
         folder.getItems().addListDataListener(this);

         ComplexEObject item;
         for (int i=0; i<folder.size(); i++)
         {
            item = (ComplexEObject) folder.get(i);
            add(getViewForItem(folder, item));
         }
      }
   
      private Component getViewForItem(Folder folder, ComplexEObject item)
      {
         return (Component) item.getIconView();
//         EView view = item.getIconView();
//         Component comp = (Component) view;
//         Association association = folder.association("items");
//         ListItemAssociation liassociation = new ListItemAssociation(association, item);
//         TransferHandler transferHandler = new BasicTransferHandler(view, liassociation);
//         comp.setTransferHandler(transferHandler);
//         return comp;
      }
   
      public void detach()
      {
         _folder.getItems().removeListDataListener(this);
         for (int i=0; i<getComponentCount(); i++)
         {
            ((EView) getComponent(i)).detach();
         }
         removeAll();
      }
   
      public void intervalAdded(final ListDataEvent e)
      {
         AbstractListEO source = (AbstractListEO) e.getSource();
         ComplexEObject eo = null;
         for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
         {
            eo = (ComplexEObject) source.getElementAt(i);
            add(getViewForItem(_folder, eo), i);
         }
      }

      public void intervalRemoved(final ListDataEvent e)
      {
         for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
         {
            ((EView) getComponent(i)).detach();
            remove(i);
         }
      }

      public void contentsChanged(final ListDataEvent e)
      {
         for (int i=0; i<getComponentCount(); i++)
            ((EView) getComponent(i)).detach();
         removeAll();

         AbstractListEO source = (AbstractListEO) e.getSource();

         ComplexEObject eo = null;
         for (int i=0; i<source.getSize(); i++)
         {
            eo = (ComplexEObject) source.getElementAt(i);
            add(getViewForItem(_folder, eo));
         }
      }
   }

   public EObject getEObject() { return _folder; }
   public boolean isMinimized() { return false; }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}

}

