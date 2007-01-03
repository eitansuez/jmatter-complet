/*
 * Created on Feb 17, 2004
 */
package com.u2d.view.swing.dnd;

import java.awt.datatransfer.*;
import java.util.logging.Logger;
import javax.swing.*;
import com.u2d.app.*;
import com.u2d.field.Association;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.AbstractListEO;
import com.u2d.view.EView;
import com.u2d.model.NullAssociation;

/**
 * handles associations - dropping a complexeobject on a null field
 * 
 * @author Eitan Suez
 */
public class DropTargetHandler extends TransferHandler
{
   Logger _tracer = Tracing.tracer();
   
   public boolean importData(JComponent c, Transferable t)
   {
      if ( canImport(c, t.getTransferDataFlavors()) )
      {
         final NullAssociation target = (NullAssociation) ((EView) c).getEObject();

         if (target instanceof ComplexEObject)
         {
            _tracer.fine("Target is " + target + "; its state is " +
                  ((ComplexEObject) target).getState().getName());
         }

         try
         {
            //DataFlavor flavor = target.type().getFlavor();
            // it's important to get the transferable's flavor because
            // the target may be a superclass type/flavor which the
            // transferable can't supply transfer data for:
            DataFlavor flavor = t.getTransferDataFlavors()[0];

            Object transferObject = t.getTransferData(flavor);
            final ComplexEObject ceo =
               (transferObject instanceof Association) ?
                       ((Association) transferObject).get() :
                       (ComplexEObject) transferObject;

            _tracer.fine("property is "+ceo+"; its state is "+ceo.getState().getName());

            // do not allow association with objects that have not been saved
            if (ceo.isTransientState())
               return false;

            new Thread()
            {
               public void run()
               {
                  target.associate(ceo);
               }
            }.start();
            return true;
         }
         catch (UnsupportedFlavorException ufe)
         {
            _tracer.info("importData: unsupported data flavor");
            return false;
         }
         catch (java.io.IOException ioe)
         {
            System.err.println("importData: I/O exception");
            return false;
         }
      }
      _tracer.fine("DropTargetHandler.importData() failed");
      return false;
   }

   public boolean canImport(JComponent c, DataFlavor[] df)
   {
      EObject target = ((EView) c).getEObject();

      // obviously this needs improvement:
      ComplexType type = (target instanceof ComplexEObject)
      ? ((ComplexEObject) target).type() :
        ((AbstractListEO) target).type();

//      DataFlavor flavor = null;
      for (int i = 0; i < df.length; i++)
      {
         if (type == null) return false;  // precaution, should not be the case..

//         flavor = type.getFlavor();

         Class targetClass = type.getJavaClass();

         Class droppedClass = df[i].getRepresentationClass();

         if (targetClass.isAssignableFrom(droppedClass))
         {
            return true;
         }
      }
      _tracer.finer("DropTargetHandler.canImport() returns false");
      return false;
   }

}

