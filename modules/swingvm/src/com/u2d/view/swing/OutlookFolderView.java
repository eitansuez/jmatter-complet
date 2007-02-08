package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.dnd.BasicTransferHandler;
import com.u2d.view.swing.list.JListView;
import com.u2d.type.composite.Folder;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.field.ListItemAssociation;
import com.u2d.field.Association;
import com.l2fprod.common.swing.JOutlookBar;
import com.l2fprod.common.swing.PercentLayout;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.beans.PropertyChangeEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 13, 2005
 * Time: 5:34:42 PM
 */
public class OutlookFolderView extends JOutlookBar implements ComplexEView
{
   private Folder _folder;
   private List<JListView> _tabs = new ArrayList<JListView>();

   public OutlookFolderView() { }

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
            JListView v = new JListView(subfolder.getItems(), true);
            v.setBorder(new LineBorder(Color.black));
            v.setOpaque(false);
            addTab(subfolder.getName().stringValue(),
                   _folder.iconSm(),
                   makeScrollPane(v));
            _tabs.add(v);
         }
      }

      setDropTarget(new DropTarget()
      {
         public synchronized void drop(DropTargetDropEvent dtde)
         {
            JListView v = _tabs.get(getSelectedIndex());
            v.getDropTarget().drop(dtde);
         }
      });
   }

   public void detach()
   {
      for (JListView v : _tabs)
      {
         v.detach();
      }
      _tabs.clear();
      removeAll();
   }


   class VerticalFolderPane extends FolderPanel
                            implements ListDataListener
   {
      VerticalFolderPane(Folder folder)
      {
         super(folder);
         setLayout(new PercentLayout(PercentLayout.VERTICAL, 0));
         setOpaque(false);
         folder.getItems().addListDataListener(this);

         ComplexEObject item;
         for (int i=0; i<folder.size(); i++)
         {
            item = (ComplexEObject) folder.get(i);
            add(getViewForItem(folder, item));
         }
      }
      
      private JComponent getViewForItem(Folder folder, ComplexEObject item)
      {
         EView view = item.getIconView();
         Association association = folder.association("items");
         ListItemAssociation liassociation = new ListItemAssociation(association, item);
         TransferHandler transferHandler = new BasicTransferHandler(view, liassociation);
         JComponent comp = (JComponent) view;
         comp.setTransferHandler(transferHandler);
         return comp;
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
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               AbstractListEO source = (AbstractListEO) e.getSource();
               ComplexEObject eo = null;
               for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
               {
                  eo = (ComplexEObject) source.getElementAt(i);
                  add(getViewForItem(_folder, eo), i);
               }

               revalidate(); repaint();
            }
         });
      }

      public void intervalRemoved(final ListDataEvent e)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
               {
                  ((EView) getComponent(i)).detach();
                  remove(i);
               }

               revalidate(); repaint();
            }
         });
      }

      public void contentsChanged(final ListDataEvent e)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
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

               revalidate(); repaint();
            }
         });

      }
   }

   public EObject getEObject() { return _folder; }
   public boolean isMinimized() { return false; }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}

}

