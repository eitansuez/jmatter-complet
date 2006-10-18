package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.field.Association;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.NullAssociation;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.event.*;
import javax.swing.ImageIcon;
import org.wings.*;
import org.wings.border.SBevelBorder;
import org.wings.border.SBorder;
import java.awt.Color;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 1:04:42 PM
 */
public class AssociationView extends CardPanel implements ComplexEView
{
   private Association _association;
   private AssociationView.AssociatedPanel _associatedPnl;
   private AssociationView.DissociatedPanel _dissociatedPnl;

   public AssociationView(Association a)
   {
      bind(a);

      _associatedPnl = new AssociationView.AssociatedPanel();
      add(_associatedPnl, _associatedPnl.name());
      _dissociatedPnl = new AssociationView.DissociatedPanel();
      add(_dissociatedPnl, _dissociatedPnl.name());

      setView();
   }

   public void bind(Association a)
   {
      if (_association != null)
         detach();

      _association = a;
      _association.addPropertyChangeListener(this);
      _association.addChangeListener(this);
   }

   public void stateChanged(ChangeEvent e)
   {
      _dissociatedPnl.stateChanged();
      _associatedPnl.stateChanged();
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if (_association.getName().equals(evt.getPropertyName()))
      {
         setView();
      }
   }

   private void setView()
   {
      AssociationView.AssocStateView stateView = (_association.isEmpty()) ? (AssociationView.AssocStateView) _dissociatedPnl : _associatedPnl;
      ComplexEObject value = _association.get();
      stateView.bind(value);
      show(stateView.name());
   }

   public Association getAssociation() { return _association; }
   public EObject getEObject() { return _association.get(); }
   public boolean isMinimized() { return false; }

   public void detach()
   {
      _association.removePropertyChangeListener(this);
      _association.removeChangeListener(this);
      _associatedPnl.detach();
      _dissociatedPnl.detach();
   }

   interface AssocStateView
   {
      public String name();
      public void bind(ComplexEObject value);
   }
   class AssociatedPanel extends SPanel implements AssociationView.AssocStateView
   {
      EView view;
      AssociationView.ItemPanel itemPnl;
      SButton dissocBtn;

      public AssociatedPanel()
      {
         setLayout(new SFlowLayout());
         itemPnl = new AssociationView.ItemPanel();
         add(itemPnl);

         dissocBtn = new IconButton(DISSOCIATE_ICON, DISSOCIATE_ROLLOVER);
         dissocBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               _association.dissociate();
            }
         });
         add(dissocBtn);
         stateChanged();
      }

      public void stateChanged()
      {
         dissocBtn.setVisible(_association.isEditableState());
      }

      public String name() { return "associated"; }

      public void bind(ComplexEObject value)
      {
         if (view != null)
         {
            view.detach();
            itemPnl.removeItem(view);
         }
         view = value.getListItemView();
         itemPnl.addItem(view);

         // TODO:
//         TransferHandler transferHandler = new AssociationTransferHandler(view, _association);
//         ((SComponent) view).setTransferHandler(transferHandler);
      }

      public void detach()
      {
         if (view != null)
         {
            view.detach();
            view = null;
         }
      }
   }


   class DissociatedPanel extends SPanel implements AssociationView.AssocStateView
   {
      EView view;
      AssociationView.ItemPanel itemPnl;
      NullAssociation nullAssoc = new NullAssociation(_association);

      public DissociatedPanel()
      {
         setLayout(new SFlowLayout());
         itemPnl = new AssociationView.ItemPanel();
         add(itemPnl);
      }

      public void stateChanged() { }

      public String name() { return "dissociated"; }
      public void bind(ComplexEObject value)
      {
         if (view != null)
         {
            view.detach();
            itemPnl.removeItem(view);
         }
         view = value.getListItemView();
         itemPnl.addItem(view);

         // TODO: really want to set this once directly on itemPnl
         //   figure out how to achieve this
//         ((JComponent) view).setTransferHandler(new DropTargetHandler());
      }

      public void detach()
      {
         if (view != null)
         {
            view.detach();
            view = null;
         }
      }
   }

   public static ImageIcon ASSOCIATE_ICON, DISSOCIATE_ICON, ASSOCIATE_ROLLOVER, DISSOCIATE_ROLLOVER;
   static
   {
      ClassLoader loader = AssociationView.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/list-add.png");
      ASSOCIATE_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-add-hover.png");
      ASSOCIATE_ROLLOVER = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/dissociate.png");
      DISSOCIATE_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/dissociate-hover.png");
      DISSOCIATE_ROLLOVER = new ImageIcon(imgURL);
   }

   class ItemPanel extends SPanel
   {
      SComponent _itemView;

      public ItemPanel()
      {
         setBackground(Color.white);
         SBorder border = new SBevelBorder(SBevelBorder.RAISED);
         setBorder(border);
      }

      public void removeItem(EView view)
      {
         _itemView = null;
         remove(0);
      }

      public void addItem(EView view)
      {
         _itemView = (SComponent) view;
         add(_itemView, 0);
      }
      
   }

}
