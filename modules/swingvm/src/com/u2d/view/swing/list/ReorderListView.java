/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.view.swing.dnd.SimpleListTransferHandler;
import com.u2d.list.RelationalList;
import com.u2d.field.ListItemAssociation;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.*;
import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * The idea is a jlistview whose elements can be reordered via dnd.
 * Useful in the context of the classbar.
 * 
 * @author Eitan Suez
 */
public class ReorderListView extends JListView
{
   public ReorderListView(AbstractListEO leo)
   {
      super(leo);
   }
   public ReorderListView(AbstractListEO leo, boolean renderCellsAsIcons)
   {
      super(leo, renderCellsAsIcons);
   }

   private ReorderDropTarget _rlDropTarget;
   private SimpleListTransferHandler _transferHandler;

   public void setupTransferHandler()
   {
      setDragEnabled(true);
      _transferHandler = new SimpleListTransferHandler(this);
      setTransferHandler(_transferHandler);
      if (_leo instanceof RelationalList)
      {
         RelationalList rl = (RelationalList) _leo;
         _rlDropTarget = new ReorderDropTarget(rl);
         setDropTarget(_rlDropTarget);
      }
   }

   public void detach()
   {
      super.detach();
      if (_rlDropTarget != null) _rlDropTarget.detach();
      if (_transferHandler != null) _transferHandler.detach();
   }


   /* adapter from relationallistdroptarget */
   class ReorderDropTarget extends DropTarget
   {
      private RelationalList _leo;

      public ReorderDropTarget(RelationalList leo)
      {
         super();
         _leo = leo;
         setup();
      }

      private void setup()
      {
         try
         {
            addDropTargetListener(new DropTargetAdapter()
            {
               public void drop(DropTargetDropEvent dtde)
               {
                  Transferable t = dtde.getTransferable();
                  DataFlavor[] flavors = t.getTransferDataFlavors();
                  for (DataFlavor flavor : flavors)
                  {
                     Class droppedType = flavor.getRepresentationClass();
                     Class folderItemsType = _leo.type().getJavaClass();

                     try
                     {
                        Point dropPt = dtde.getLocation();

                        int index = locationToIndex(dropPt);
                        Component c = getCellRenderer().getListCellRendererComponent(ReorderListView.this,
                              _leo.get(index), index, false, false);
                        double rowHeight = c.getPreferredSize().getHeight();
                        int spotIndex = (int) Math.round(dropPt.getY() / rowHeight);

                        if (flavor.equals(ListItemAssociation.FLAVOR))
                        {
                           ListItemAssociation lia = (ListItemAssociation) t.getTransferData(flavor);
                           ComplexEObject item = lia.item();
                           int itemIndex = _leo.getItems().indexOf(item);
                           boolean movingdown = itemIndex < spotIndex;
                           index  = (movingdown) ? spotIndex - 1 : spotIndex;

                           boolean reOrdering = (index != itemIndex);
                           if (reOrdering)
                           {
                              _leo.remove(item);
                              _leo.add(index, item);
                              if (!_leo.parentObject().isEditableState())
                                 _leo.parentObject().save();
                           }
                           break;
                        }
                        else if (folderItemsType.isAssignableFrom(droppedType))
                        {
                           ComplexEObject item = (ComplexEObject) t.getTransferData(flavor);
                           _leo.add(index, item);
                           if (!_leo.parentObject().isEditableState())
                              _leo.parentObject().save();
                           break;
                        }
                     }
                     catch (UnsupportedFlavorException ex)
                     {
                        // should not happen since the flavor comes from
                        // gettransferdataflavors..
                        System.err.println("UnsupportedFlavorException: " + ex.getMessage());
                     }
                     catch (IOException ex)
                     {
                        System.err.println("IO Exception: " + ex.getMessage());
                     }
                  }
               }
            });
         }
         catch (TooManyListenersException ex)
         {
           System.err.println("TooManyListenersException: "+ex.getMessage());
           ex.printStackTrace();
         }
      }

      public void detach()
      {
         _leo = null;
      }
   }
   
}
