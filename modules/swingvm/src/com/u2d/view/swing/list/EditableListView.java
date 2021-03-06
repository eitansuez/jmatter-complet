package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.AppLoader;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.NullAssociation;
import com.u2d.list.RelationalList;
import com.u2d.ui.IconButton;
import com.u2d.ui.MenuButton;
import com.u2d.ui.desktop.Positioning;
import com.u2d.element.Command;
import com.u2d.field.IndexedField;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 19, 2005
 * Time: 11:28:10 AM
 *
 * Wraps/Decorates JListView
 */
public class EditableListView extends JPanel
                              implements ListEView, CompositeView, ListSelectionListener
{
   private RelationalList _leo;
   private JListView _listView;
   private JButton _removeBtn;
   private NullAssociation _association = null;
   private JPanel _northPanel;
   private JPopupMenu _menu = new JPopupMenu();

   public EditableListView(RelationalList leo)
   {
      _leo = leo;
      _association = new NullAssociation(_leo);
      _listView = (JListView) SwingViewMechanism.getInstance().getListView(_leo);
      _listView.setVisibleRowCount(Math.min(leo.getSize()+1, 5));
      
      setLayout(new BorderLayout());
      setOpaque(false);

      if (!leo.field().isReadOnly())
      {
         add(northPanel(), BorderLayout.PAGE_START);
      }
//      _leo.parentObject().addChangeListener(this);
//      setEditable(_leo.parentObject().isEditableState());
      
      JScrollPane scrollPane = new JScrollPane(_listView);
      add(scrollPane, BorderLayout.CENTER);

      // why was i doing this??
//      // allow drop on list to add item to list..
//      _listView.setDropTarget(null); // clear default drop target handling..
//      _listView.setTransferHandler(new DropTargetHandler());

      setBorder(BorderFactory.createTitledBorder(_leo.field().getNaturalPath()));

      _listView.addListSelectionListener(this);
      
      _leo.addListDataListener(this);  // for the purpose of dynamically resizing the height of the
        // contained listview component as a function of the number of children it contains.

      if ( !_leo.parentObject().isEditableState() && noEdit())
      {
         setEditable(false);
      }
   }

   private boolean noEdit()
   {
      Command editCmd = _leo.parentObject().command("Edit", _leo.parentObject().getState());
      return  editCmd == null || _leo.currentUser().getRole().hasRestrictionOnCmd(editCmd);
   }

   public void valueChanged(ListSelectionEvent e)
   {
      if (e.getValueIsAdjusting()) return;
      adjustRemoveBtnEnabled();
   }

   private void adjustRemoveBtnEnabled()
   {
      boolean selected = (_listView.selectedEO() != null);
      if (_removeBtn != null)
      {
         _removeBtn.setEnabled(selected);
      }
   }

   private JPanel northPanel()
   {
      _northPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
      _northPanel.setOpaque(false);
      _northPanel.add(addBtn());

      IndexedField field = (IndexedField) _leo.field();
      // don't add "-" button if item's association to this field is required
      if (! (field.isBidirectionalRelationship() &&
             field.inverseField().required()) )
      {
         _northPanel.add(removeBtn());
      }

      return _northPanel;
   }

   private JButton removeBtn()
   {
      _removeBtn = new IconButton(DEL_ICON, DEL_ROLLOVER);
      adjustRemoveBtnEnabled();

      _removeBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            AppLoader.getInstance().newThread(new Runnable()
            {
               public void run()
               {
                  dissociateItem();
               }
            }).start();
         }
      });

      return _removeBtn;
   }
   
   private void dissociateItem()
   {
      EObject item = _listView.selectedEO();
      if (item == null) return;
      ComplexEObject eo = (ComplexEObject) item;
      _leo.dissociate(eo);
   }

   private JButton addBtn()
   {
      addMenuItem("New");
      if (!((IndexedField) _leo.field()).ownsChildren())
      {
         addMenuItemForBrowseCmd();
         addMenuItem("Find");
      }
      return new MenuButton(ADD_ICON, ADD_ROLLOVER, _menu);
   }

   private void addMenuItemForBrowseCmd()
   {
      if (!_association.isIndexedAssociation())
      {
         addMenuItem("Browse");
         return;
      }

      Command cmd = _association.command("Browse");
      if (cmd.isForbidden(_association))
      {
         return;
      }

      CommandAdapter action = new CommandAdapter(cmd, _association, this)
      {
         @Override
         public void actionPerformed(ActionEvent evt)
         {
            final JComponent sourceComp = (JComponent) _source;
            if (_command.blocks())
            {
               sourceComp.setEnabled(false);
            }

            SwingViewMechanism vmech = SwingViewMechanism.getInstance();
            vmech.displayViewFor(vmech.getMultiPickView(_association.getAsList()), _source, Positioning.NEARMOUSE);

            if (_command.blocks())
            {
               sourceComp.setEnabled(true);  // if the view has closed or detached, then still have a ref to source
                 // to re-enable it
            }
         }
      };
      _menu.add(new JMenuItem(action));
   }

   private void addMenuItem(String cmdName)
   {
      Command cmd = _association.command(cmdName);
      if (!cmd.isForbidden(_association))
      {
         CommandAdapter action = new CommandAdapter(cmd, _association, this);
         _menu.add(new JMenuItem(action));
      }
   }

   Insets _insets = new Insets(20, 5, 5, 5);
   public Insets getInsets() { return _insets; }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
//      _leo.parentObject().removeChangeListener(this);
      _listView.removeListSelectionListener(this);
      _listView.detach();
      _leo.removeListDataListener(this);

      for (int i=0; i< _menu.getComponentCount(); i++)
      {
         ((CommandAdapter) ((JMenuItem) _menu.getComponent(i)).getAction()).detach();
      }
   }

   public void stateChanged(ChangeEvent e)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            setEditable(_leo.parentObject().isEditableState());
         }
      });
   }
   
   private void setEditable(boolean editable)
   {
      _northPanel.setVisible(editable);
   }

   public void intervalAdded(ListDataEvent e) { updateListRowHeight(); }
   public void intervalRemoved(ListDataEvent e) { updateListRowHeight(); }
   public void contentsChanged(ListDataEvent e) { updateListRowHeight(); }
   
   private void updateListRowHeight()
   {
      _listView.setVisibleRowCount(Math.min(_leo.getSize()+1, 5));
   }

   public boolean isMinimized() { return false; }

   public EView getInnerView() { return _listView; }


   public static ImageIcon ADD_ICON, DEL_ICON, ADD_ROLLOVER, DEL_ROLLOVER;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/list-add.png");
      ADD_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-add-hover.png");
      ADD_ROLLOVER = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-remove.png");
      DEL_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-remove-hover.png");
      DEL_ROLLOVER = new ImageIcon(imgURL);
   }


   private static int MinWidth = 250;
   public Dimension getPreferredSize()
   {
      Dimension base = super.getPreferredSize();
      return new Dimension(MinWidth, base.height);
   }

}
