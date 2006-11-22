package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.type.composite.Folder;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.l2fprod.common.swing.JOutlookBar;
import com.l2fprod.common.swing.PercentLayout;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.beans.PropertyChangeEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
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
            JPanel pnl = new VerticalFolderPane(subfolder);
            addTab(subfolder.getName().stringValue(),
                   folder.iconSm(),
                   makeScrollPane((JComponent) pnl));
            _tabs.add(pnl);
         }
      }

      setDropTarget(new DropTarget()
      {
         public synchronized void drop(DropTargetDropEvent dtde)
         {
            VerticalFolderPane vfp = (VerticalFolderPane) 
                  _tabs.get(getSelectedIndex());
            vfp.getDropTarget().drop(dtde);
         }
      });
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
            add((JComponent) item.getIconView());
         }
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
                  add((JComponent) eo.getIconView(), i);
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
                  add((JComponent) eo.getIconView());
               }

               revalidate(); repaint();
            }
         });

      }
   }

   public EObject getEObject() { return _folder; }

   public void detach()
   {
   }

   public boolean isMinimized() { return false; }

   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}

}

