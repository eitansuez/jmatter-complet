/*
 * Created on Oct 28, 2003
 */
package com.u2d.view.swing.dnd;

import java.awt.datatransfer.*;
import javax.swing.*;
import com.u2d.model.ComplexEObject;
import com.u2d.ui.DragAdapter;
import com.u2d.view.*;

/**
 * initiate drag for association
 * 
 * @author Eitan Suez
 */
public class EOTransferHandler extends TransferHandler
{
   public EOTransferHandler(ComplexEView view)
   {
      DragAdapter dragAdapter = new DragAdapter(TransferHandler.COPY);
      ((JComponent) view).addMouseMotionListener(dragAdapter);
   }
   
   
   ComplexEView _view;
   /**
    * This variant allows a part of a view to be draggable (comp)
    */
   public EOTransferHandler(JComponent comp, ComplexEView view)
   {
      _view = view;
      DragAdapter dragAdapter = new DragAdapter(TransferHandler.COPY);
      comp.addMouseMotionListener(dragAdapter);
   }
   
	protected Transferable createTransferable(JComponent source)
	{
      if (source instanceof ComplexEView)
      {
         ComplexEView view = (ComplexEView) source;
         return (ComplexEObject) view.getEObject();
      }
      else if (_view != null)
      {
         return (ComplexEObject) _view.getEObject();
      }
      throw new RuntimeException("Source component is not a ComplexEView");
	}

	protected void exportDone(JComponent c, Transferable t, int action)
	{
		// noop
	}

	public int getSourceActions(JComponent c)
	{
		return TransferHandler.COPY_OR_MOVE;
	}
    
}
