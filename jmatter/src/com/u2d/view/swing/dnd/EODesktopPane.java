/*
 * Created on Feb 17, 2004
 */
package com.u2d.view.swing.dnd;

import java.awt.datatransfer.*;
import javax.swing.*;
import com.u2d.field.Association;
import com.u2d.app.Tracing;

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
            try
            {
               final Association association = (Association) t.getTransferData(Association.FLAVOR);
               new Thread()
               {
                  public void run()
                  {
                     association.dissociate();
                  }
               }.start();
               return true;
            }
            catch (UnsupportedFlavorException ufe)
            {
               Tracing.tracer().info("importData: unsupported data flavor");
               return false;
            }
            catch (java.io.IOException ioe)
            {
               System.err.println("importData: I/O exception");
               return false;
            }
         }
         Tracing.tracer().fine("VacantDropHandler.importData() failed");
         return false;
      }

      public boolean canImport(JComponent c, DataFlavor[] df)
      {
         for (int i = 0; i < df.length; i++)
         {
            if (df[i].equals(Association.FLAVOR)) return true;
         }
         //System.out.println("VacantDropHandler.canImport() returns false");
         return false;
      }

   }

}
