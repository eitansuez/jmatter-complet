/*
 * Created on Oct 28, 2003
 */
package com.u2d.view.swing.dnd;

import java.awt.datatransfer.*;
import javax.swing.*;
import com.u2d.ui.DragAdapter;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class BasicTransferHandler
      extends TransferHandler
{
   private Transferable _ball;
   
   public BasicTransferHandler(EView view, Transferable ball)
   {
      _ball = ball;
      ((JComponent) view).addMouseMotionListener(new DragAdapter(TransferHandler.COPY));
   }
   
	protected Transferable createTransferable(JComponent source)
	{
		return _ball;
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
