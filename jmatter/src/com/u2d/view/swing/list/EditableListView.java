package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.dnd.DropTargetHandler;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.NullAssociation;
import com.u2d.list.RelationalList;
import com.u2d.ui.IconButton;
import com.u2d.ui.MenuButton;
import com.u2d.element.Command;
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

   public EditableListView(RelationalList leo)
   {
      _leo = leo;
      _association = new NullAssociation(_leo);
      _listView = new JListView(_leo);

      setLayout(new BorderLayout());
      setOpaque(false);

      add(northPanel(), BorderLayout.NORTH);

      JScrollPane scrollPane = new JScrollPane(_listView);
      add(scrollPane, BorderLayout.CENTER);

      // allow drop on list to add item to list..
      _listView.setTransferHandler(new DropTargetHandler());

      setBorder(BorderFactory.createTitledBorder(_leo.field().getNaturalPath()));

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

   private JPanel northPanel()
   {
      JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      northPanel.setOpaque(false);
      northPanel.add(addBtn());
      northPanel.add(removeBtn());
      return northPanel;
   }

   private JButton removeBtn()
   {
      _removeBtn = new IconButton(DEL_ICON, DEL_ROLLOVER);
      adjustRemoveBtnEnabled();

      _removeBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            new Thread()
            {
               public void run()
               {
                  dissociateItem();
               }
            }.start();
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
      JPopupMenu menu = new JPopupMenu();
      menu.add(createItem());
      menu.add(browseItem());
      menu.add(findItem());
      return new MenuButton(ADD_ICON, ADD_ROLLOVER, menu);
   }

   private JMenuItem createItem() { return menuItem("New"); }
   private JMenuItem browseItem() { return menuItem("Browse"); }
   private JMenuItem findItem() { return menuItem("Find"); }

   private JMenuItem menuItem(String cmdName)
   {
      Command cmd = _association.command(cmdName);
      CommandAdapter action = new CommandAdapter(cmd, _association, this);
      return new JMenuItem(action);
   }

   public Insets getInsets()
   {
      return new Insets(10, 10, 10, 10);
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
