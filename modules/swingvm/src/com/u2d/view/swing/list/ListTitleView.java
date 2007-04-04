/*
 * Created on Oct 8, 2003
 */
package com.u2d.view.swing.list;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.view.*;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * @author Eitan Suez
 */
public class ListTitleView extends JLabel implements ListEView
{
   protected AbstractListEO _leo;
   private transient CommandsContextMenuView _cmdsView;
   
   public ListTitleView(AbstractListEO leo, EView parentView)
   {
      _leo = leo;
      _leo.addChangeListener(this);
      _leo.addListDataListener(this);
      
      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_leo, this, parentView);

      setFocusable(true);
      setHorizontalAlignment(JLabel.LEFT);
      setVerticalAlignment(JLabel.CENTER);
      setHorizontalTextPosition(JLabel.RIGHT);
      setVerticalTextPosition(JLabel.CENTER);
      setOpaque(false);
      
      ComponentStyle.addClass(this, "list-title");
      
      updateTitle();
      setIcon(_leo.iconLg());
   }
   
   public void contentsChanged(ListDataEvent evt) { updateTitle(); }
   public void intervalAdded(ListDataEvent evt) { updateTitle(); }
   public void intervalRemoved(ListDataEvent evt) { updateTitle(); }
   
   // TODO: solve all this with an aspect
   private void updateTitle()
   {
      if (!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               setText(_leo.toString());
            }
         });
      }
      else
      {
         setText(_leo.toString());
      }
   }
   
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
   private Insets _insets = new Insets(2, 5, 6, 8);
	public Insets getInsets() { return _insets; }
   
   public EObject getEObject() { return _leo; }
   
	public Dimension getMinimumSize() { return getPreferredSize(); }
	public Dimension getMaximumSize() { return getPreferredSize(); }
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();
		d.width += getInsets().left + getInsets().right;
		d.height += getInsets().top + getInsets().bottom;
		return d;
	}
	
   public void detach()
   {
      _cmdsView.detach();
      _leo.removeChangeListener(this);
      _leo.removeListDataListener(this);
   }
   
   public boolean isMinimized() { return false; }

}
