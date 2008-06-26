package com.u2d.view.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.u2d.app.Tracing;
import com.u2d.element.Field;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.NullComplexEObject;
import com.u2d.view.EView;

final class TreeNodeAdapter extends DefaultMutableTreeNode implements EView, PropertyChangeListener, ListDataListener
{
	private JTreeView jtree = null;

	TreeNodeAdapter(EObject eo)
	{
		super();
		setEO(eo);
	}
   TreeNodeAdapter(TreeNodeAdapter parentNode, EObject eo)
   {
      this(eo);
      if (seenInAncestors(parentNode, eo)) setAllowsChildren(false);
   }

   private void setEO(EObject eo)
   {
      this.setUserObject(eo);
      if (eo != null)
      {
         eo.addChangeListener(this);
      }
   }

   private boolean isBuilt = false;
   protected boolean isBuilt() { return isBuilt; }
   protected synchronized void buildSubNodes()
   {
      if (isBuilt || !getAllowsChildren()) return;

      EObject eo = (EObject) getUserObject();
      if (eo instanceof AbstractListEO)
      {
         AbstractListEO leo = (AbstractListEO) eo;
         leo.addListDataListener(this);
         for (int i = 0; i < leo.getSize(); i++)
         {
            EObject child = leo.get(i);
            TreeNodeAdapter node = new TreeNodeAdapter(this, child);
            this.add(node);
         }
      }
      else if ((eo instanceof ComplexEObject) && !(eo instanceof NullComplexEObject))
      {
         ComplexEObject ceo = (ComplexEObject) eo;
         for (int i = 0; i < ceo.childFields().size(); i++)
         {
            Field field = (Field) ceo.childFields().get(i);
            if (field.isAtomic() || field.isChoice())
               continue;
            if (field.isAssociation())
            {
               ceo.addPropertyChangeListener(field.name(), this);
            }
            this.add(new TreeNodeAdapter(this, field.get(ceo)));
         }
      }

      isBuilt = true;
   }

   public EObject getEObject() { return (EObject)getUserObject(); }
	
	private boolean seenInAncestors(TreeNodeAdapter tParent, EObject eo)
	{
      if (tParent == null) return false;
      if (tParent.getEObject().equals(eo))
      {
         return true;
      }

      Object gParent = tParent.getParent();
		if(gParent == null) { return false; }
		return seenInAncestors( (TreeNodeAdapter)gParent, eo);		
	}

   public void stateChanged(ChangeEvent e)
   {
      if (jtree != null) this.getTreeModel().nodeChanged(this);
   }

	protected void bind(JTreeView jtree) { this.jtree = jtree; }

   public void detach()
   {
      EObject eo = getEObject();
      if (eo != null)
      {
         eo.removeChangeListener(this);
         if (eo instanceof AbstractListEO)
            ((AbstractListEO) eo).removeListDataListener(this);
         if (eo instanceof ComplexEObject)
            ((ComplexEObject) eo).removePropertyChangeListener(this);
      }
      this.setUserObject(null);
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      ComplexEObject newValue = (ComplexEObject) evt.getNewValue();
      if (newValue == null) return;
      Field field = newValue.field();
      
      for (int i = 0; i < this.getChildCount(); i++)
      {
         TreeNodeAdapter node = (TreeNodeAdapter) getChildAt(i);
         EObject eo = (EObject) node.getUserObject();
         if (eo.field().equals(field))
         {
            node.detach();
            this.getTreeModel().removeNodeFromParent(node);

            TreeNodeAdapter newNode = new TreeNodeAdapter(this, newValue);
            this.getTreeModel().insertNodeInto(newNode, this, i);
            this.getTreeModel().nodeChanged(newNode);
         }
      }
   }

   private DefaultTreeModel getTreeModel() { return (DefaultTreeModel) jtree.getModel(); }

   public void contentsChanged(ListDataEvent e)
   {
      EObject eo = (EObject) getUserObject();
      if ((eo instanceof AbstractListEO)
            && (ListDataEvent.CONTENTS_CHANGED == e.getType()))
      {
         Tracing.tracer().info("A complicated list change event fired from a list.");
         AbstractListEO leo = (AbstractListEO) eo;
         this.removeAllChildren();
         for (int i = 0; i < leo.getSize(); i++)
         {
            if (!seenInAncestors(this, leo.get(i)))
            {
               this.add(new TreeNodeAdapter(leo.get(i)));
            }
         }
         this.getTreeModel().nodeChanged(this);
      }
   }

   public void intervalAdded(ListDataEvent e)
   {
      EObject eo = (EObject) getUserObject();
      if (eo instanceof AbstractListEO)
      {
         AbstractListEO leo = (AbstractListEO) eo;
         if (ListDataEvent.INTERVAL_ADDED == e.getType())
         {
            for (int i = e.getIndex0(); i <= e.getIndex1(); i++)
            {
               this.getTreeModel().insertNodeInto(new TreeNodeAdapter(leo.get(i)), this, i);
            }
         }
      }
   }

   public void intervalRemoved(ListDataEvent e)
   {
      EObject eo = (EObject) getUserObject();
      if (eo instanceof AbstractListEO)
      {
         if (ListDataEvent.INTERVAL_REMOVED == e.getType())
            for (int i = e.getIndex1(); i >= e.getIndex0(); i--)
            {
               TreeNodeAdapter iNode = (TreeNodeAdapter) this.getChildAt(i);
               iNode.detach();
               this.getTreeModel().removeNodeFromParent(iNode);
            }
      }
   }
}
