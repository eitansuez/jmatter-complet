/*
 * Created on Oct 14, 2003
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.*;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.SeeThruTree;
import com.u2d.view.ComplexEView;
import com.u2d.view.Selectable;

/**
 * @author Eitan Suez
 */
public class JTreeView extends SeeThruTree implements ComplexEView, Selectable
{
   private ComplexEObject _ceo;

   public JTreeView(ComplexEObject ceo)
   { 
      super(new TreeNodeAdapter(ceo));
      _ceo = ceo;

      ToolTipManager.sharedInstance().registerComponent(this);
      setCellRenderer(new JmTreeCellRenderer());
      setShowsRootHandles(true);
      getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

      SwingViewMechanism.setupEnterKeyBinding(this);
   }
   
   class JmTreeCellRenderer extends DefaultTreeCellRenderer
   {
      JmTreeCellRenderer() { super(); }

      public Component getTreeCellRendererComponent(JTree tree, Object node,
				boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus)
      {
			super.getTreeCellRendererComponent(tree, node, isSelected, expanded, leaf, row, hasFocus);
         TreeNodeAdapter tNode = (TreeNodeAdapter) node;
			tNode.bind(JTreeView.this);
			if(!tNode.isBuilt()) tNode.buildSubNodes();
			EObject eo = tNode.getEObject();
         if (eo != null)
         {
            setIcon(eo.iconSm());
            setText(eo.title().toString());
            if (eo.field() != null) { setToolTipText(eo.field().name()); }
         }
         highlight(tree, this, isSelected, hasFocus);
         return this;
		}

		private Border _noBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

		private void highlight(JTree tree, JComponent comp, boolean isSelected, boolean hasFocus)
      {
			comp.setBackground(isSelected ? UIManager.getColor("Tree.selectionBackground") : tree.getBackground());
			comp.setForeground(isSelected ? UIManager.getColor("Tree.selectionForeground") : tree.getForeground());
			comp.setBorder(hasFocus ? UIManager.getBorder("List.focusCellHighlightBorder") : _noBorder);
      }
   }
   
   public boolean isMinimized() { return false; }

   public void detach() { }

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
