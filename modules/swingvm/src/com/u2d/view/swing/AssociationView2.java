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
      AssocStateView stateView = (_association.isEmpty()) ? _dissociatedPnl : _associatedPnl;
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

   abstract class CustomPnl extends JPanel
   {
      protected ItemPanel itemPnl;

      abstract JButton assocDissocBtn();

      public CustomPnl()
      {
         setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
         setOpaque(false);
         itemPnl = new ItemPanel();
         add(itemPnl);
      }

      protected void customizeFocusBehavior()
      {
         itemPnl.setFocusable(true);
         itemPnl.addFocusListener(new FocusAdapter()
         {
            public void focusGained(FocusEvent e)
            {
               if (!_association.isEditableState()) return;

               if (!SwingUtilities.isDescendingFrom(e.getOppositeComponent(), AssociationView2.this))
               {
                  itemPnl.enterEditState();
               }
               else if (SwingUtilities.isDescendingFrom(e.getOppositeComponent(), itemPnl))
               {
                  itemPnl.returnToReadState();
                  FocusTraversalPolicy policy = getFocusCycleRootAncestor().getFocusTraversalPolicy();
                  Component before = policy.getComponentBefore(getFocusCycleRootAncestor(), itemPnl);
                  before.requestFocus();
               }
            }
         });

         assocDissocBtn().setFocusable(true);
         assocDissocBtn().addFocusListener(new FocusAdapter()
         {
            public void focusGained(FocusEvent e)
            {
               if (SwingUtilities.isDescendingFrom(e.getOppositeComponent(), itemPnl))
               {
                  itemPnl.returnToReadState();
               }
            }

            public void focusLost(FocusEvent e)
            {
               if (SwingUtilities.isDescendingFrom(e.getOppositeComponent(), itemPnl))
               {
                  itemPnl.enterEditState();
               }
            }
         });
      }
   }
   class AssociatedPanel extends CustomPnl implements AssocStateView, AppEventListener
   {
      protected JButton dissocBtn;
      protected EView view;

      JButton assocDissocBtn() { return dissocBtn; }

      public AssociatedPanel()
      {
         super();
         
         dissocBtn = new IconButton(DISSOCIATE_ICON, DISSOCIATE_ROLLOVER);
         dissocBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               AppLoader.getInstance().newThread(new Runnable()
               {
                  public void run()
                  {
                     _association.dissociate();
                  }
               }).start();
            }
         });
         add(dissocBtn);
         stateChanged();
         customizeFocusBehavior();
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
   }


   class DissociatedPanel extends CustomPnl implements AssocStateView
   {
      EView view;
      MenuButton assocBtn;
      NullAssociation nullAssoc = new NullAssociation(_association);

      public DissociatedPanel()
      {
         super();
         add(assocBtn());
         stateChanged();
         customizeFocusBehavior();
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
         addMenuItem(menu, "New");
         addMenuItem(menu, "Browse");
         addMenuItem(menu, "Find");
         assocBtn = new MenuButton(ASSOCIATE_ICON, ASSOCIATE_ROLLOVER, menu);
         assocBtn.setFocusable(true);
         assocBtn.setFocusPainted(true);
         return assocBtn;
      }

      private void addMenuItem(JPopupMenu menu, String cmdName)
      {
         Command cmd = nullAssoc.command(cmdName);
         if (!cmd.isForbidden(nullAssoc))
         {
            cmd.localize(ComplexType.forClass(User.class));
            CommandAdapter action = new CommandAdapter(cmd, nullAssoc, AssociationView2.this);
            menu.add(new JMenuItem(action));
         }
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
         JPopupMenu menu = assocBtn.menu();
         for (int i=0; i<menu.getComponentCount(); i++)
         {
            JMenuItem item = (JMenuItem) menu.getComponent(i);
            CommandAdapter cmdAdapter = (CommandAdapter) item.getAction();
            cmdAdapter.detach();
         }
         if (view != null)
         {
            view.detach();
            view = null;
         }
      }
      
      public JButton assocDissocBtn() { return assocBtn; }
   }

   public static ImageIcon ASSOCIATE_ICON, DISSOCIATE_ICON, ASSOCIATE_ROLLOVER, DISSOCIATE_ROLLOVER;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
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
         _editableComp.updateState();
         return _editableComp;
      }

      private void returnToReadState() { changeEditableState(true); }

      private void enterEditState()
      {
         if (_association.isReadOnly()) return;
         changeEditableState(false);
      }

      private void changeEditableState(boolean read)
      {
         if (read == inViewState) return; // already in the right state.
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
