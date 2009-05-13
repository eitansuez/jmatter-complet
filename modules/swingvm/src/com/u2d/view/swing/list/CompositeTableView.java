package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.swing.AppLoader;
import com.u2d.list.CompositeList;
import com.u2d.model.EObject;
import com.u2d.ui.IconButton;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 24, 2006
 * Time: 12:37:20 PM
 */
public class CompositeTableView extends JPanel
      implements ListEView, CompositeView
{
   private CompositeList _leo;
   private TableView _tableView;
   private JButton _addBtn, _removeBtn;
   private JPanel northPanel;
   private PropertyChangeListener readOnlyChangeListener;

   public CompositeTableView(CompositeList leo)
   {
      _leo = leo;
      _tableView = new TableView(_leo);

      if (_leo.parentObject() != null)
      {
         _leo.parentObject().addChangeListener(this);
      }
      
      setLayout(new BorderLayout());
      setOpaque(false);

      add(northPanel(), BorderLayout.PAGE_START);
      northPanel.setVisible(!_leo.field().isReadOnly());
      readOnlyChangeListener = new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            northPanel.setVisible(!_leo.field().isReadOnly());
         }
      };
      _leo.field().addPropertyChangeListener("readOnly", readOnlyChangeListener);

      JScrollPane scrollPane = new JScrollPane(_tableView);
      add(scrollPane, BorderLayout.CENTER);

      setBorder(BorderFactory.createTitledBorder(_leo.field().getNaturalPath()));

      _tableView.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         public void valueChanged(ListSelectionEvent e)
         {
            if (e.getValueIsAdjusting()) return;
            adjustRemoveBtnEnabled();
         }
      });
   }

   private void adjustRemoveBtnEnabled()
   {
      boolean selected = (_tableView.getSelectedRowCount() > 0);
      _removeBtn.setEnabled(selected);
   }

   private JPanel northPanel()
   {
      northPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
      northPanel.setOpaque(false);
      northPanel.add(addBtn());
      northPanel.add(removeBtn());
      return northPanel;
   }

   private JButton addBtn()
   {
      _addBtn = new IconButton(ADD_ICON, ADD_ROLLOVER);
      _addBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            AppLoader.getInstance().newThread(new Runnable()
            {
               public void run()
               {
                  _leo.AddItem(null);
                  SwingUtilities.invokeLater(new Runnable()
                  {  // ensures that table resizes appropriately
                     public void run()
                     {
                        revalidate(); repaint();
                     }
                  });
               }
            }).start();
         }
      });
      stateChanged(null);  // set enabled = fn(editable)
      return _addBtn;
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
                  _leo.remove(_tableView.selectedEO());
                  SwingUtilities.invokeLater(new Runnable()
                  {  // ensures that table resizes appropriately
                     public void run()
                     {
                        revalidate(); repaint();
                     }
                  });
               }
            }).start();
         }
      });

      return _removeBtn;
   }
   
   public Insets getInsets() { return new Insets(10, 10, 10, 10); }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      if (_leo.parentObject() != null)
      {
         _leo.parentObject().removeChangeListener(this);
      }
      _leo.field().removePropertyChangeListener("readOnly", readOnlyChangeListener);
   }

   public void stateChanged(ChangeEvent e)
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            if (_leo.parentObject() != null)
            {
               _addBtn.setEnabled(_leo.parentObject().isEditableState());
            }
         }
      });
   }

   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

   public boolean isMinimized() { return false; }

   public EView getInnerView() { return _tableView; }


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

}
