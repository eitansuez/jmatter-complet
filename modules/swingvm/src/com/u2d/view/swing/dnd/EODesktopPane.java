/*
 * Created on Feb 17, 2004
 */
package com.u2d.view.swing.dnd;

import java.awt.datatransfer.*;
import javax.swing.*;
import com.u2d.field.Association;
import com.u2d.field.ListItemAssociation;
import com.u2d.field.Dissociable;
import com.u2d.app.Tracing;
import com.u2d.view.swing.AppLoader;

/**
 * An EODesktopPane is currently defined as a desktop pane that can
 * handle vacant drop gestures (which it interprets as dissociation
 * requests)
 * 
 * @author Eitan Suez
 */
public class EODesktopPane extends com.u2d.ui.desktop.EnhDesktopPane
{
   public EODesktopPane()
   {
      super();
      setTransferHandler(new VacantDropHandler());
   }

   public java.awt.Dimension getPreferredSize()
   {
      return new java.awt.Dimension(500,500);
   }

   class VacantDropHandler extends TransferHandler
   {
      public boolean importData(JComponent c, Transferable t)
      {
         if ( canImport(c, t.getTransferDataFlavors()) )
         {
            boolean success = tryToImport(t, Association.FLAVOR);
            if (!success)
            {
               success = tryToImport(t, ListItemAssociation.FLAVOR);
            }
            return success;
         }
         Tracing.tracer().fine("VacantDropHandler.importData() failed");
         return false;
      }
      
      private boolean tryToImport(Transferable t, DataFlavor flavor)
      {
         try
         {
            final Dissociable d = (Dissociable) t.getTransferData(flavor);
               
            AppLoader.getInstance().newThread(new Runnable()
            {
               public void run()
               {
                  d.dissociate();
               }
            }).start();
            return true;
         }
         catch (UnsupportedFlavorException ufe)
         {
            // wrong message..
//            Tracing.tracer().info("importData: unsupported data flavor");
         }
         catch (java.io.IOException ioe)
         {
            System.err.println("importData: I/O exception");
         }
         return false;
      }

      public boolean canImport(JComponent c, DataFlavor[] df)
      {
         for (int i = 0; i < df.length; i++)
         {
            if (df[i].equals(Association.FLAVOR)) return true;
            if (df[i].equals(ListItemAssociation.FLAVOR)) return true;
         }
         return false;
      }

   }

}
