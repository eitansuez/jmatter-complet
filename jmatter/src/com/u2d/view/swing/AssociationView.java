/*
 * Created on March 3, 2004
 */
package com.u2d.view.swing;

import com.u2d.field.Association;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.beans.*;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.*;
import com.u2d.ui.desktop.CloseableJInternalFrame;

/**
 * @author Eitan Suez
 */
public class AssociationView extends JPanel 
                             implements ComplexEView, PopupClient
{
   private Association _association;
   private ComplexEView _view;
   private PopupButton2 _trigger;
   private JComponent _contents;
   
   
   public AssociationView(Association association)
   {
      _association = association;
      _association.addPropertyChangeListener(this);
      _association.addChangeListener(this);
      
      setLayout(new FlowLayout(FlowLayout.LEFT));
      setOpaque(false);

//      setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

      ComplexEObject value = _association.get();
      _view = value.getListItemView();
      add((JComponent) _view);
      
      _trigger = new PopupButton2(_association.iconSm(), this, this, this);
      add(_trigger);
      
      // TODO: add a dissociate command to context menu

      // TODO: handle case where association is to an interface

      dndIt();
      stateChanged(null);
   }
   
	public void propertyChange(PropertyChangeEvent evt)
   {
      if (_association.getName().equals(evt.getPropertyName()))
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               // replace the view for the association..
               _view.detach();
               remove(0);
               ComplexEObject value = _association.get();
               _view = value.getListItemView();
               add((JComponent) _view, 0);
               dndIt();
               revalidate(); repaint();
               CloseableJInternalFrame.updateSize(AssociationView.this);
            }
         });
      }
   }
   
   private void dndIt()
   {
      JComponent comp = ((JComponent) _view);
      // stateful behavior (could use cleaning up):
      if (_association.isEmpty())
      {
         comp.setTransferHandler(new DropTargetHandler());
      }
      else
      {
         TransferHandler transferHandler = new AssociationTransferHandler(_view, _association);
         comp.setTransferHandler(transferHandler);
      }
   }
   
	public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _trigger.setVisible(_association.isEditableState());
         }
      });
   }

   public Association getAssociation() { return _association; }
   // question: shouldn't geteobject return an Association?
   public EObject getEObject() { return _association.get(); }
   public boolean isMinimized() { return false; }
   
   
   /**
    * implementation of PopupClient.  trigger delegates to this method
    * when it's time for it to show a list of items to pick from.
    */
   public JComponent getContents()
   {
      if (_contents == null)
      {
         AbstractListEO pickList = _association.type().Browse(null);
         ListEView listView = pickList.getPickView();
         JList list = null;
         if (listView instanceof CompositeView)
         {
            list = (JList) ((CompositeView) listView).getInnerView();
         }
         else
         {
            list = (JList) listView;
         }
         final JList jlist = list;
         list.addListSelectionListener(new ListSelectionListener()
            {
               public void valueChanged(ListSelectionEvent evt)
               {
                  ComplexEObject selectedItem = (ComplexEObject) jlist.getSelectedValue();
                  _association.set(selectedItem);
                  SwingUtilities.invokeLater(new Runnable()
                  {
                     public void run()
                     {
                        _trigger.hidePopup();
                        CloseableJInternalFrame.updateSize(AssociationView.this);
                     }
                  });
               }
            });
         if (listView instanceof CompositeView)
         {
            _contents = (JComponent) listView;
         }
         else
         {
            _contents = new JScrollPane(list);
         }
      }
      return _contents;
   }
   
   public void detach()
   {
      _association.removePropertyChangeListener(this);
      _association.removeChangeListener(this);
      _view.detach();
   }

}
