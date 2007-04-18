/*
 * Created on Oct 16, 2003
 */
package com.u2d.view.swing;

import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Eitan Suez
 */
public class ExpandableView extends JPanel implements ComplexEView, Editor
{
	private ComplexEObject _ceo;
	private ExpandCollapseButton _toggleBtn;
	private ListItemView _handle;
	private FormView _leaf;
   private CellConstraints cc;
   
	public ExpandableView()
	{
      setOpaque(false);
      
      FormLayout layout = new FormLayout("pref,pref", "pref,pref");
      setLayout(layout);
      cc = new CellConstraints();
      
      _toggleBtn = new ExpandCollapseButton();
      add(_toggleBtn, cc.xy(1, 1));
      _toggleBtn.addActionListener( new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               expandCollapse(evt.getActionCommand());
            }
         });
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

      _handle = (ListItemView) _ceo.getListItemView();
      add((JComponent) _handle, cc.xy(2, 1));

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
      if (_leaf == null) _leaf = new FormView(_ceo, true);
      return _leaf;
   }
   
   public int transferValue() { return leaf().transferValue(); }
   public int validateValue() { return leaf().validateValue(); }

   public void setEditable(boolean editable) { leaf().setEditable(editable); }
   public boolean isEditable() { return leaf().isEditable(); }
   
   public boolean isExpanded() { return _toggleBtn.isExpanded(); }
   
   private void expandCollapse(String which)
   {
      expandCollapse("+".equals(which)); 
   }
   
   private synchronized void expandCollapse(boolean expand)
	{
      if (expand && _toggleBtn.isCollapsed())
      {
         add(leaf(), cc.xy(2, 2));
      }
      else if (!expand && _toggleBtn.isExpanded())
      {
         remove(_leaf);
      }
      _toggleBtn.toggle(expand);
      CloseableJInternalFrame.updateSize(this);
	}
	
	class ExpandCollapseButton extends JButton
	{
		ExpandCollapseButton()
		{
         setOpaque(false);
			setIcon(EXPAND_ICON);
			setActionCommand("+");
			setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
			setContentAreaFilled(false);  // causes no painting of background of button when pressed
			// icon change is now visual cue of press so no need for content
         // area filling (looks kind of out of place when set to true)
			setFocusPainted(false);
         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		void toggle()
      {
         String newActionCommand = (isCollapsed()) ? "-" : "+";
         ImageIcon icon = (isCollapsed()) ? COLLAPSE_ICON : EXPAND_ICON;
         setIcon(icon);
         setActionCommand(newActionCommand);
      }
      
      void toggle(boolean expanded)
      {
         if (expanded != isExpanded())
            toggle();
      }
      
      boolean isExpanded() { return "-".equals(getActionCommand()); }
      boolean isCollapsed() { return !isExpanded(); }
		String getState() { return getActionCommand(); }
		
		private Insets _insets = new Insets(2,5,2,5);
		public Insets getInsets() { return _insets; }
	}

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return true; }
   public void propertyChange(java.beans.PropertyChangeEvent evt) { }
   public void stateChanged(ChangeEvent evt) { }

	
   static ImageIcon EXPAND_ICON, COLLAPSE_ICON;
   static ImageIcon EXPAND_ROLLOVER, COLLAPSE_ROLLOVER;
   static
   {
      ClassLoader loader = ExpandableView.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/expand.gif");
      EXPAND_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/collapse.gif");
      COLLAPSE_ICON = new ImageIcon(imgURL);
   }
	

}
