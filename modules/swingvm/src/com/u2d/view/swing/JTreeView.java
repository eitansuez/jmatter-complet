/*
 * Created on Oct 14, 2003
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.*;
import java.util.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.SeeThruTree;
import com.u2d.view.ComplexEView;
import com.u2d.view.Selectable;
import com.u2d.view.EView;
import com.u2d.app.Context;

/**
 * @author Eitan Suez
 */
public class JTreeView extends SeeThruTree implements ComplexEView, TreeCellRenderer, Selectable
{
   private ComplexEObject _ceo;

   public JTreeView(ComplexEObject ceo)
   {
      _ceo = ceo;
      setModel(ceo.treeModel());

      ToolTipManager.sharedInstance().registerComponent(this);
      setCellRenderer(this);
      //setEditable(true);  // enables tooltips [fixed in java5 so commented out]
      setShowsRootHandles(true);
      getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      SwingViewMechanism.setupEnterKeyBinding(this);
   }

   private Map _components = new HashMap();

   public Component getTreeCellRendererComponent(JTree tree, Object node, boolean isSelected,
                                                 boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      if (_components.get(node) != null)
      {
         JComponent comp = (JComponent) _components.get(node);
         return highlight(tree, comp, isSelected, hasFocus);
      }

      EObject eo = (EObject) node;

      JComponent comp = null;
      if (eo instanceof ComplexEObject)
      {
         ComplexEObject ceo = (ComplexEObject) node;
         comp = (JComponent) Context.getInstance().swingvmech().
               getListItemViewAdapter(ceo);
      }
      else if (eo instanceof AbstractListEO)
      {
         AbstractListEO leo = (AbstractListEO) node;
         comp = new com.u2d.view.swing.list.ListItemViewAdapter(leo);
      }

      if (eo.field() != null)
         comp.setToolTipText(eo.field().label());

      _components.put(node, comp);
      return highlight(tree, comp, isSelected, hasFocus);
   }

   private Border _noBorder = BorderFactory.createEmptyBorder(1,1,1,1);
   private JComponent highlight(JTree tree, JComponent comp, boolean isSelected, boolean hasFocus)
   {
      comp.setBackground( isSelected ? UIManager.getColor("Tree.selectionBackground") : tree.getBackground() );
      comp.setForeground( isSelected ? UIManager.getColor("Tree.selectionForeground") : tree.getForeground() );
      comp.setBorder( hasFocus ? UIManager.getBorder("List.focusCellHighlightBorder") : _noBorder );
      return comp;
   }


   public boolean isMinimized() { return false; }

   public void detach()
   {
      Iterator itr = _components.values().iterator();
      EView item;
      while (itr.hasNext())
      {
         item = (EView) itr.next();
         item.detach();
      }
   }

   public EObject getEObject() { return _ceo; }
   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}

   public ComplexEObject selectedEO()
   {
      Object lastPathComponent = getSelectionPath().getLastPathComponent();
      if (lastPathComponent instanceof ComplexEObject)
         return (ComplexEObject) lastPathComponent;
      return null;
   }

}
