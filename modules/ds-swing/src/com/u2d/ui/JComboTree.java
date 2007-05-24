/*
 * Created on May 11, 2004
 */
package com.u2d.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.tree.*;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class JComboTree extends JPanel implements ActionListener
{
   private JButton _trigger;
   private JPopupMenu _menu;
   private TreeModel _model;
   private LinkedList _selectedPath;
   private boolean _customTrigger = false;
   
   public JComboTree(TreeModel model)
   {
      JButton trigger = new JButton(EXPAND_ICON);
      trigger.setMargin(new Insets(0,6,0,6));
      trigger.setHorizontalTextPosition(SwingConstants.LEFT);
      trigger.setHorizontalAlignment(SwingConstants.CENTER);
      trigger.setOpaque(false);
      
      init(model, trigger, false);
   }
   
   public JComboTree(TreeModel model, JButton trigger)
   {
      init(model, trigger, true);
   }
   
   private void init(TreeModel model, JButton trigger, boolean custom)
   {
      setOpaque(false);
      setLayout(new BorderLayout());
      
      _trigger = trigger;
      add(_trigger, BorderLayout.CENTER);

      _model = model;
      _menu = new JPopupMenu();
      
      populateMenu(_menu, startPath());

      _trigger.addActionListener(this);
      _customTrigger = custom;
   }

   public void actionPerformed(ActionEvent e)
   {
      _menu.show(JComboTree.this, 0, _trigger.getSize().height);
   }

   private LinkedList startPath()
   {
      LinkedList startPath = new LinkedList();
      startPath.add(_model.getRoot());
      return startPath;
   }

   private void populateMenu(JComponent menu, LinkedList path)
   {
      Object parent = path.getLast();
      
      for (int i=0; i<_model.getChildCount(parent); i++)
      {
         Object item = _model.getChild(parent, i);
         if (_model.isLeaf(item))
         {
            LinkedList leafPath = new LinkedList(path);
            leafPath.addLast(item);
            ComboTreeItem ctItem = new ComboTreeItem(leafPath);
            menu.add(ctItem);
         }
         else
         {
            LinkedList branchPath = new LinkedList(path);
            branchPath.addLast(item);
            JMenu childMenu = new JMenu(item.toString());
            populateMenu(childMenu, branchPath);
            menu.add(childMenu);
         }
      }
   }
   
   public void selectFirst()
   {
      setSelectedPath(firstLeafPath());
   }
   
   public LinkedList firstLeafPath()
   {
      LinkedList startpath = new LinkedList();
      startpath.addLast(_model.getRoot());
      return firstLeafPath(startpath);
   }
   
   public LinkedList firstLeafPath(LinkedList path)
   {
      Object parent = path.getLast();
      for (int i=0; i<_model.getChildCount(parent); i++)
      {
         Object item = _model.getChild(parent, i);
         if (_model.isLeaf(item))
         {
            path.addLast(item);
            return path;
         }
      }
      // if did not find a leaf among immediate children,
      // then recurse to first leaf of an aggregate child
      for (int i=0; i<_model.getChildCount(parent); i++)
      {
         Object item = _model.getChild(parent, i);
         if (!_model.isLeaf(item))
         {
            path.addLast(item);
            return firstLeafPath(path);
         }
      }
      return null;
   }
   
   public LinkedList getSelectedPath() { return _selectedPath; }
   public Object getSelectedItem() { return _selectedPath.getLast(); }
   
   public void setSelectedPath(LinkedList path)
   {
      if (_selectedPath == path) return;

      _selectedPath = path;
//      System.out.println("Selected path is: "+_selectedPath);
      if (!_customTrigger)
         _trigger.setText(_selectedPath.getLast().toString());
      fireActionEvent();
   }
   
   class ComboTreeItem extends JMenuItem implements ActionListener
   {
      LinkedList _path;
      ComboTreeItem(LinkedList path)
      {
         super(path.getLast().toString());
         _path = path;
         addActionListener(this);
      }
      
      public void actionPerformed(ActionEvent evt)
      {
         setSelectedPath(_path);
      }
      
   }
   
   public static Icon EXPAND_ICON;
   static
   {
      ClassLoader loader = JComboTree.class.getClassLoader();
      URL imgURL = loader.getResource("images/open.png");
      EXPAND_ICON = new ImageIcon(imgURL);
   }
   
   
   private ActionListener subscribers = null;

   public synchronized void addActionListener(ActionListener l)
   {
      subscribers = AWTEventMulticaster.add(subscribers, l);
   }

   public synchronized void removeActionListener(ActionListener l)
   {
      subscribers = AWTEventMulticaster.remove(subscribers, l);
   }

   private void fireActionEvent()
   {
      if (subscribers != null)
         subscribers.actionPerformed(new ActionEvent(this,
               ActionEvent.ACTION_PERFORMED, ""));
   }


   /* this code should not occur on removenotify but during proper
            cleanup sequence.
      TODO: reimplement removing actionlistener properly
      */
//   public void removeNotify()
//   {
//      super.removeNotify();
//      if (_trigger != null)
//      {
//         _trigger.removeActionListener(this);
//         remove(_trigger);
//         _trigger = null;
//      }
//   }

}
