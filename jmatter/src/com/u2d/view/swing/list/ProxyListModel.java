package com.u2d.view.swing.list;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

/**
 * Ok, basically I believe JList is flawed.
 * I believe that it's the duty of the list data
 * listener to ensure that the list redrawing takes
 * place on the edt.  But JList's BasicListUI's 
 * listdatalistener does not!
 * 
 * Instead it forces you to put swing-related code
 * in the model layer, as in:
 *   invokelater:  model.addItem
 * 
 * so, i have no choic but to wrap my model object
 * in a ui-specific model object so i can intercept
 * the calls and get back on the edt..
 * 
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 10, 2006
 * Time: 11:38:17 AM
 */
public class ProxyListModel extends AbstractListModel
      implements ListDataListener
{
   ListModel _listModel;
   ProxyListModel(ListModel listModel)
   {
      _listModel = listModel;
      _listModel.addListDataListener(this);
   }
   public int getSize() { return _listModel.getSize(); }
   public Object getElementAt(int index) { return _listModel.getElementAt(index); }
   
   public void detach()
   {
      _listModel.removeListDataListener(this);
   }


   public void intervalAdded(final ListDataEvent e)
   {
      if (!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               fireIntervalAdded(e.getSource(), e.getIndex0(), e.getIndex1());
            }
         });
      }
      else
      {
         fireIntervalAdded(e.getSource(), e.getIndex0(), e.getIndex1());
      }
   }

   public void intervalRemoved(final ListDataEvent e)
   {
      if (!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               fireIntervalRemoved(e.getSource(), e.getIndex0(), e.getIndex1());
            }
         });
      }
      else
      {
         fireIntervalRemoved(e.getSource(), e.getIndex0(), e.getIndex1());
      }
   }

   public void contentsChanged(final ListDataEvent e)
   {
      if (!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               fireContentsChanged(e.getSource(), e.getIndex0(), e.getIndex1());
            }
         });
      }
      else
      {
         fireContentsChanged(e.getSource(), e.getIndex0(), e.getIndex1());
      }
   }
}
