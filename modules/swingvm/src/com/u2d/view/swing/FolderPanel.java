package com.u2d.view.swing;

import com.u2d.type.composite.Folder;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 22, 2005
 * Time: 11:54:48 PM
 * 
 * One of the virtues of a FolderPanel is that it should
 * act as a droptarget for folder items..
 */
public class FolderPanel extends JPanel
{
   protected Folder _folder;

   public FolderPanel(Folder folder)
   {
      _folder = folder;
      setOpaque(true);
      setBackground(Color.white);
      setupToAddItemOnDrop(this);
   }

   public EObject getEObject() { return _folder; }
   public boolean isMinimized() { return false; }

   protected void setupToAddItemOnDrop(JComponent target)
   {
      DropTarget dropTarget = new DropTarget();
      try
      {
         dropTarget.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               Transferable t = dtde.getTransferable();
               DataFlavor[] flavors = t.getTransferDataFlavors();
               for (int i=0; i<flavors.length; i++)
               {
                  Class droppedType = flavors[i].getRepresentationClass();
                  Class folderItemsType = _folder.getItems().type().getJavaClass();
                  if (folderItemsType.isAssignableFrom(droppedType))
                  {
                     try
                     {
                        ComplexEObject item = (ComplexEObject)
                                                t.getTransferData(flavors[i]);
                        _folder.getItems().add(item);
                        if (!_folder.isEditableState())
                           _folder.save();
                        break;
                     }
                     catch (UnsupportedFlavorException ex)
                     {
                        // should not happen since the flavor comes from
                        // gettransferdataflavors..
                        System.err.println("UnsupportedFlavorException: "+ex.getMessage());
                     }
                     catch (IOException ex)
                     {
                        System.err.println("IO Exception: "+ex.getMessage());
                     }
                  }
               }
            }
         });
         target.setDropTarget(dropTarget);
      }
      catch (TooManyListenersException ex)
      {
        System.err.println("TooManyListenersException: "+ex.getMessage());
        ex.printStackTrace();
      }
   }

}
