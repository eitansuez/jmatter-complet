package com.u2d.view.swing.dnd;

import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.list.RelationalList;
import com.u2d.field.ListItemAssociation;
import com.u2d.view.SelectableListView;
import javax.swing.*;
import java.awt.datatransfer.Transferable;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 16, 2007
 * Time: 11:27:09 AM
 */
public class SimpleListTransferHandler extends TransferHandler
{
   SelectableListView _listview;
   
   public SimpleListTransferHandler(SelectableListView slv)
   {
      _listview = slv;
   }
   protected Transferable createTransferable(JComponent source)
   {
      ComplexEObject item = _listview.selectedEO();
      AbstractListEO leo = (AbstractListEO) _listview.getEObject();
      if (leo instanceof RelationalList)
      {
         RelationalList rl = (RelationalList) leo;
         return new ListItemAssociation(rl.association(), item);
      }
      else
      {
         return item;
      }
   }
   public int getSourceActions(JComponent c) { return COPY_OR_MOVE; }
   protected void exportDone(JComponent c, Transferable t, int action)
   {
      // noop
   }
}
   
