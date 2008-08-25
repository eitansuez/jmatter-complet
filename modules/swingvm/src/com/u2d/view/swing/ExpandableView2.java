package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.GradientPanel;
import javax.swing.event.ChangeEvent;
import java.awt.*;

import net.miginfocom.swing.MigLayout;

/**
 * A more "hip" version of Expandable view.
 * 
 * Entire view is surrounded by a border / box.  Item View is also
 * bordered with a gradient background fill.  "Old-style" tree '+/-'
 * replaced with more modern arrow down/arrow up on rhs.
 * 
 * @author Eitan Suez
 */
public class ExpandableView2
      extends GenericExpandableView
      implements ComplexEView, Editor
{
	private ComplexEObject _ceo;
	private ListItemView _handle;
	private FormView _leaf;
   
	public ExpandableView2()
	{
   }
   
   public void bind(ComplexEObject ceo)
   {
      boolean expanded = (ceo.field() == null) || defaultExpand(ceo);
      bind(ceo, expanded);
   }

   private boolean defaultExpand(ComplexEObject ceo)
   {
      return ceo.field().required() && ceo.isEditableState();
   }

   public void bind(ComplexEObject ceo, boolean expanded)
   {
      _ceo = ceo;
      _handle = new ListItemView();
      _handle.bind(_ceo, this);

      GradientPanel gp = new GradientPanel(new Color(0x5171FF), false);
      MigLayout layout = new MigLayout("insets 0, fill");
      gp.setLayout(layout);
      gp.add(_handle, "alignx leading, grow");
      gp.add(_toggleBtn, "alignx trailing");
      
      add(gp);
      
      if (_toggleBtn.isExpanded() != expanded)
         _toggleBtn.doClick();
   }
	
   public void detach()
   {
      _handle.detach();
      remove(_handle);
      
      if (_leaf != null)
      {
         expandCollapse(false);
         _leaf.detach();
         _leaf = null; // for now, until formviews become poolable
      }
   }

   private FormView leaf()
   {
      if (_leaf == null)
      {
         _leaf = new FormView(_ceo, true);
      }
      return _leaf;
   }
   
   public int transferValue() { return leaf().transferValue(); }
   public int validateValue() { return leaf().validateValue(); }

   public void setEditable(boolean editable) { leaf().setEditable(editable); }
   public boolean isEditable() { return leaf().isEditable(); }
   
   public boolean isExpanded() { return _toggleBtn.isExpanded(); }
   
   protected synchronized void expandCollapse(boolean expand)
	{
      if (expand && _toggleBtn.isCollapsed())
      {
         add(leaf());
      }
      else if (!expand && _toggleBtn.isExpanded())
      {
         remove(_leaf);
      }
      _toggleBtn.toggle(expand);
      CloseableJInternalFrame.updateSize(this);
	}
	
   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return true; }
   public void propertyChange(java.beans.PropertyChangeEvent evt) { }
   public void stateChanged(ChangeEvent evt) { }

}
