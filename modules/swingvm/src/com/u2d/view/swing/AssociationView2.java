package com.u2d.view.swing;

import com.u2d.ui.CardPanel;
import com.u2d.ui.IconButton;
import com.u2d.ui.MenuButton;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.dnd.DropTargetHandler;
import com.u2d.view.swing.dnd.BasicTransferHandler;
import com.u2d.field.Association;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.NullAssociation;
import com.u2d.app.User;
import com.u2d.element.Command;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import static com.u2d.pubsub.AppEventType.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 1:04:42 PM
 */
public class AssociationView2 extends CardPanel implements ComplexEView
{
   private Association _association;
   private AssociatedPanel _associatedPnl;
   private DissociatedPanel _dissociatedPnl;

   public AssociationView2(Association a)
   {
      bind(a);

      _associatedPnl = new AssociatedPanel();
      add(_associatedPnl, _associatedPnl.name());
      _dissociatedPnl = new DissociatedPanel();
      add(_dissociatedPnl, _dissociatedPnl.name());
      // work in progress:
//      
//      setFocusable(true);
//      addFocusListener(new FocusAdapter()
//      {
//         public void focusGained(FocusEvent e)
//         {
//            if (_association.isEditableState())
//            {
//               AssocStateView stateView = (_association.isEmpty()) ? (AssocStateView) _dissociatedPnl : _associatedPnl;
//               stateView.enterEditState();
//            }
//         }
//      });

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
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _dissociatedPnl.stateChanged();
            _associatedPnl.stateChanged();
         }
      });
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if (_association.getName().equals(evt.getPropertyName()))
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               setView();
            }
         });
      }
   }

   private void setView()
   {
      AssocStateView stateView = (_association.isEmpty()) ? (AssocStateView) _dissociatedPnl : _associatedPnl;
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
      public void enterEditState();
   }
   class AssociatedPanel extends JPanel implements AssocStateView, AppEventListener
   {
      EView view;
      ItemPanel itemPnl;
      JButton dissocBtn;

      public AssociatedPanel()
      {
         setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
         setOpaque(false);
         itemPnl = new ItemPanel();
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
         boolean editable = _association.isEditableState();
         if (!editable)
         {
            dissocBtn.setVisible(editable);
            itemPnl.returnToReadState();
         }
         else if (!_association.isReadOnly())
         {
            dissocBtn.setVisible(editable);
         }
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
         
         value.addAppEventListener(DELETE, this);

         TransferHandler transferHandler = new BasicTransferHandler(view, _association);
         ((JComponent) view).setTransferHandler(transferHandler);

         revalidate(); repaint();
         CloseableJInternalFrame.updateSize(AssociationView2.this);
      }

      public void detach()
      {
         if (view != null)
         {
            ((ComplexEObject) view.getEObject()).removeAppEventListener(DELETE, this);
            view.detach();
            view = null;
         }
      }


      public void onEvent(AppEvent evt)
      {
         ((ComplexEObject) view.getEObject()).removeAppEventListener(DELETE, this);
         _association.dissociate();
      }


      public void enterEditState() { itemPnl.enterEditState(); }
   }


   class DissociatedPanel extends JPanel implements AssocStateView
   {
      EView view;
      ItemPanel itemPnl;
      JButton assocBtn;
      NullAssociation nullAssoc = new NullAssociation(_association);

      public DissociatedPanel()
      {
         setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
         setOpaque(false);
         itemPnl = new ItemPanel();
         add(itemPnl);
         add(assocBtn());
         stateChanged();
      }

      public void stateChanged()
      {
         boolean editable = _association.isEditableState();
         if (!editable)
         {
            assocBtn.setVisible(editable);
            itemPnl.returnToReadState();
         }
         else if (!_association.isReadOnly())
         {
            assocBtn.setVisible(editable);
         }
      }

      private JButton assocBtn()
      {
         JPopupMenu menu = new JPopupMenu();
         menu.add(menuItem("New"));
         menu.add(menuItem("Browse"));
         menu.add(menuItem("Find"));
         assocBtn = new MenuButton(ASSOCIATE_ICON, ASSOCIATE_ROLLOVER, menu);
         return assocBtn;
      }

      private JMenuItem menuItem(String cmdName)
      {
         Command cmd = nullAssoc.command(cmdName);
         cmd.localize(ComplexType.forClass(User.class));
         CommandAdapter action = new CommandAdapter(cmd, nullAssoc, AssociationView2.this);
         return new JMenuItem(action);
      }

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
         ((JComponent) view).setTransferHandler(new DropTargetHandler());

         revalidate(); repaint();
         CloseableJInternalFrame.updateSize(AssociationView2.this);
      }

      public void detach()
      {
         if (view != null)
         {
            view.detach();
            view = null;
         }
      }
      
      public void enterEditState() { itemPnl.enterEditState(); }
   }

   public static ImageIcon ASSOCIATE_ICON, DISSOCIATE_ICON, ASSOCIATE_ROLLOVER, DISSOCIATE_ROLLOVER;
   static
   {
      ClassLoader loader = AssociationView2.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/list-add.png");
      ASSOCIATE_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-add-hover.png");
      ASSOCIATE_ROLLOVER = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/dissociate.png");
      DISSOCIATE_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/dissociate-hover.png");
      DISSOCIATE_ROLLOVER = new ImageIcon(imgURL);
   }

   class ItemPanel extends JPanel implements MouseListener
   {
      boolean inViewState = true;
      Component _itemView;
      AssociationEO _editableComp;

      public ItemPanel()
      {
         setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));
         setBackground(Color.white);
         setOpaque(true);
         Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
         setBorder(border);
      }

      private Component editableComp()
      {
         if (_editableComp == null)
         {
            _editableComp = new AssociationEO(_association);
            _editableComp.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  if (_editableComp != null)
                     _editableComp.bindValue();
                  returnToReadState();
               }
            });
         }
         return _editableComp;
      }

      private void returnToReadState() { changeEditableState(true); }
      private void enterEditState() { changeEditableState(false); }

      private void changeEditableState(boolean read)
      {
         if (getComponentCount() > 0)
         {
            remove(0);
            Component comp = (read) ? _itemView : editableComp();
            add(comp, 0);
            inViewState = read;
            com.u2d.ui.desktop.CloseableJInternalFrame.updateSize(this);
         }
      }

      public void mouseClicked(MouseEvent e)
      {
         if (e.getClickCount() == 1 && !e.isPopupTrigger())
         {
            if (_association.isEditableState() && inViewState)
            {
               enterEditState();
            }
         }
      }
      public void mousePressed(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { } 
      public void mouseEntered(MouseEvent e) { } 
      public void mouseExited(MouseEvent e) { }

      public void removeItem(EView view)
      {
         ((JComponent) view).removeMouseListener(this);
         _itemView = null;
         remove(0);
      }

      public void addItem(EView view)
      {
         _itemView = (Component) view;
         add(_itemView, 0);
         _itemView.addMouseListener(this);
      }
      
   }

}
