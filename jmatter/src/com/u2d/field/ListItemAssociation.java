package com.u2d.field;

import com.u2d.model.ComplexEObject;
import java.io.Serializable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2006
 * Time: 9:25:24 PM
 */
public class ListItemAssociation implements Transferable, Serializable, Dissociable
{
   private Association _association;
   private ComplexEObject _item;
   
   public ListItemAssociation(Association association, ComplexEObject item)
   {
      _item = item;
      _association = association;
   }
   
   public void dissociate()
   {
      _association.dissociateItem(_item);
   }
   public ComplexEObject item() { return _item; }

   // ========== implementation of Transferrable Interface  ===============
   
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
   {
      if (!isDataFlavorSupported(flavor))
          throw new UnsupportedFlavorException(flavor);
      return this;
   }
   
   public DataFlavor[] getTransferDataFlavors()
   {
      DataFlavor typeFlavor = makeFlavor(getClass());
      return new DataFlavor[] {FLAVOR, typeFlavor };
   }
   
   public boolean isDataFlavorSupported(DataFlavor f)
   {
      return f.equals(FLAVOR);
   }

   public static DataFlavor FLAVOR;
   static
   {
      FLAVOR = makeFlavor(ListItemAssociation.class);
   }
   
   private static DataFlavor makeFlavor(Class cls)
   {
      try
      {
         String flavorType = DataFlavor.javaJVMLocalObjectMimeType + 
         ";class="+cls.getName();
         return new DataFlavor(flavorType);
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         throw new RuntimeException("Failed to find class while attempting "+ 
               "to construct a data flavor for it! ("+cls+")");
      }
   }


}
