package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.wings.IconButton;
import com.u2d.view.wings.MenuButton;
import com.u2d.view.wings.CommandAdapter;
import com.u2d.list.RelationalList;
import com.u2d.model.NullAssociation;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.element.Command;
import javax.swing.event.*;
import java.awt.event.*;
import org.wings.*;
import org.wings.border.*;
import javax.swing.ImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 19, 2005
 * Time: 11:28:10 AM
 *
 * Wraps/Decorates SListView
 */
public class EditableListView
      extends SPanel
      implements ListEView, CompositeView, ListSelectionListener
{
   private RelationalList _leo;
   private SListView _listView;
   private SButton _removeBtn;
   private NullAssociation _association = null;

   public EditableListView(RelationalList leo)
   {
      _leo = leo;
      _association = new NullAssociation(_leo);
      _listView = new SListView(_leo);

      setLayout(new SBorderLayout());

      add(northPanel(), SBorderLayout.NORTH);
      add(_listView, SBorderLayout.CENTER);

      // allow drop on list to add item to list..
//      _listView.setTransferHandler(new DropTargetHandler());

      setBorder(new STitledBorder(_leo.field().getNaturalPath()));

      _listView.addListSelectionListener(this);
   }

   public void valueChanged(ListSelectionEvent e)
   {
      if (e.getValueIsAdjusting()) return;
      adjustRemoveBtnEnabled();
   }

   private void adjustRemoveBtnEnabled()
   {
      boolean selected = (_listView.selectedEO() != null);
      _removeBtn.setEnabled(selected);
   }

   private SPanel northPanel()
   {
      SPanel northPanel = new SPanel(new SFlowLayout(SFlowLayout.LEFT));
      northPanel.add(addBtn());
      northPanel.add(removeBtn());
      return northPanel;
   }

   private SButton removeBtn()
   {
      _removeBtn = new IconButton(DEL_ICON, DEL_ROLLOVER);
      adjustRemoveBtnEnabled();

      _removeBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            dissociateItem();
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

   private SButton addBtn()
   {
      SPopupMenu menu = new SPopupMenu();
      menu.add(createItem());
      menu.add(browseItem());
      menu.add(findItem());
      return new MenuButton(ADD_ICON, ADD_ROLLOVER, menu);
   }

   private SMenuItem createItem() { return menuItem("New"); }
   private SMenuItem browseItem() { return menuItem("Browse"); }
   private SMenuItem findItem() { return menuItem("Find"); }

   private SMenuItem menuItem(String cmdName)
   {
      Command cmd = _association.command(cmdName);
      CommandAdapter action = new CommandAdapter(cmd, _association, this);
      return new SMenuItem(action);
   }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      _listView.removeListSelectionListener(this);
      _listView.detach();
   }

   public void stateChanged(ChangeEvent e) { }

   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

   public boolean isMinimized() { return false; }

   public EView getInnerView() { return _listView; }


   public static ImageIcon ADD_ICON, DEL_ICON, ADD_ROLLOVER, DEL_ROLLOVER;
   static
   {
      ClassLoader loader = EditableListView.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/list-add.png");
      ADD_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-add-hover.png");
      ADD_ROLLOVER = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-remove.png");
      DEL_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/list-remove-hover.png");
      DEL_ROLLOVER = new ImageIcon(imgURL);
   }

}
