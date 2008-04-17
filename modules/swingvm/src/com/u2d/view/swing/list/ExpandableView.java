/*
 * Created on Oct 16, 2003
 */
package com.u2d.view.swing.list;

import com.u2d.model.EObject;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import com.u2d.list.RelationalList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Eitan Suez
 */
public class ExpandableView extends JPanel implements ListEView
{
	private RelationalList _leo;
	private ExpandCollapseButton _toggleBtn;
	private EView _handle;
	private EditableListView _leaf;
   private CellConstraints cc;
   
	public ExpandableView(RelationalList leo)
	{
      init(leo);

      boolean expanded = false;
      if (_leo.field() == null)
         expanded = true;
      else
         expanded = _leo.field().required() && inEditableState();
      toggle(expanded);
   }

   private boolean inEditableState()
   {
      return _leo.parentObject() != null &&
             _leo.parentObject().isEditableState();
   }

   public ExpandableView(RelationalList leo, boolean expanded)
   {
      init(leo);
      toggle(expanded);
   }
   
   private void init(RelationalList leo)
   {
      _leo = leo;
      setOpaque(false);
      
      _handle = new ListItemView(_leo);
      
      FormLayout layout = new FormLayout("pref,pref", "pref,pref");
      setLayout(layout);
      cc = new CellConstraints();
      
      _toggleBtn = new ExpandCollapseButton();
      
      add(_toggleBtn, cc.xy(1, 1));
      add((JComponent) _handle, cc.xy(2, 1));
      
      _toggleBtn.addActionListener( new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               expandCollapse(evt.getActionCommand());
            }
         });
   }
	
	public void toggle(boolean yes)
	{
      if (yes) _toggleBtn.doClick();
	}
	
   private EditableListView leaf()
   {
      if (_leaf == null) _leaf = new EditableListView(_leo);
      return _leaf;
   }
   
   public boolean isExpanded() { return _toggleBtn.isExpanded(); }
   
	private void expandCollapse(String which)
	{
      boolean expand = ("+".equals(which)); 
		_toggleBtn.toggle();
		if (expand)
      {
         add(leaf(), cc.xy(2, 2));
      }
      else
      {
         remove(2);
      }
      CloseableJInternalFrame.updateSize(this);
	}
	
	public void stateChanged(ChangeEvent evt)
	{
	}
	
	public EObject getEObject() { return _leo; }
   
   
	static ImageIcon EXPAND_ICON, COLLAPSE_ICON;
   static ImageIcon EXPAND_ROLLOVER, COLLAPSE_ROLLOVER;
	static
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		java.net.URL imgURL = loader.getResource("images/expand.gif");
		EXPAND_ICON = new ImageIcon(imgURL);
		imgURL = loader.getResource("images/collapse.gif");
		COLLAPSE_ICON = new ImageIcon(imgURL);
//      imgURL = loader.getResource("images/expand_rollover.png");
//      EXPAND_ROLLOVER = new ImageIcon(imgURL);
//      imgURL = loader.getResource("images/collapse_rollover.png");
//      COLLAPSE_ROLLOVER = new ImageIcon(imgURL);
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
//         setRolloverEnabled(true);
         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		void toggle()
      {
         String newActionCommand = (isCollapsed()) ? "-" : "+";
         ImageIcon icon = (isCollapsed()) ? COLLAPSE_ICON : EXPAND_ICON;
//         ImageIcon rolloverIcon = 
//            (isCollapsed()) ? COLLAPSE_ROLLOVER : EXPAND_ROLLOVER;
         setIcon(icon);
//         setRolloverIcon(rolloverIcon);
         setActionCommand(newActionCommand);
      }
      
      boolean isExpanded()
      {
         return "-".equals(getActionCommand());
      }
      boolean isCollapsed() { return !isExpanded(); }
		
		String getState()
		{
			return getActionCommand();
		}
		
		private Insets _insets = new Insets(2,5,2,5);
		public Insets getInsets() { return _insets; }
	}

//   public void removeNotify()
//   {
//      super.removeNotify();
//      _ceo.removeChangeListener(this);
//   }

   public boolean isMinimized() { return true; }

   public void detach()
   {
      if (_handle != null) _handle.detach();
      if (_leaf != null) _leaf.detach();
   }

   // i don't really need to know when items are added or removed
   // handle and leaf do and they're notified directly..
   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }
}
