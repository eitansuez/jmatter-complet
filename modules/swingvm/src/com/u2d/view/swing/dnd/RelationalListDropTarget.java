package com.u2d.view.swing.dnd;

import com.u2d.list.RelationalList;
import com.u2d.model.ComplexEObject;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 21, 2007
 * Time: 9:44:41 AM
 */
public class RelationalListDropTarget extends DropTarget
{
   private RelationalList _leo;
      
   public RelationalListDropTarget(RelationalList leo)
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
               for (int i=0; i<flavors.length; i++)
               {
                  Class droppedType = flavors[i].getRepresentationClass();
                  Class folderItemsType = _leo.type().getJavaClass();
                  if (folderItemsType.isAssignableFrom(droppedType))
                  {
                     try
                     {
                        ComplexEObject item = (ComplexEObject)
                                                t.getTransferData(flavors[i]);
                        _leo.add(item);
                        if (!_leo.parentObject().isEditableState())
                           _leo.parentObject().save();
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
