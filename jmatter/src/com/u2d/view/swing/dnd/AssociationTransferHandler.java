/*
 * Created on Oct 28, 2003
 */
package com.u2d.view.swing.dnd;

import java.awt.datatransfer.*;
import javax.swing.*;
import com.u2d.field.Association;
import com.u2d.ui.DragAdapter;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class AssociationTransferHandler extends TransferHandler
{
   private Association _association;
   
   public AssociationTransferHandler(EView view, Association association)
   {
      _association = association;
      ((JComponent) view).addMouseMotionListener(new DragAdapter(TransferHandler.COPY));
   }
   
	protected Transferable createTransferable(JComponent source)
	{
		return _association;
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
