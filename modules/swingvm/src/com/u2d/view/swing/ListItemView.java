/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.element.Command;
import com.u2d.ui.UIUtils;

/**
 * @author Eitan Suez
 */
public class ListItemView extends JLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;
   private MouseListener _defaultActionListener;
   
   public ListItemView()
   {
      setHorizontalAlignment(JLabel.LEFT);
      setVerticalAlignment(JLabel.CENTER);
      setHorizontalTextPosition(JLabel.RIGHT);
      setOpaque(false);

      setTransferHandler(new EOTransferHandler(this));  // set up as drag source
      
      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(ComplexEObject ceo)
   {
      _ceo = ceo;
      setText(_ceo.title().toString());
      setIcon(_ceo.iconSm());

      _cmdsView.bind(ceo, this);

      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

      Command defaultCmd = _ceo.defaultCommand();
      CommandAdapter defaultAction = new CommandAdapter(defaultCmd, _ceo, this);
      _defaultActionListener = UIUtils.doubleClickActionListener(defaultAction);
      addMouseListener(_defaultActionListener);
   }

   public void detach()
   {
      _cmdsView.detach();
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
      removeMouseListener(_defaultActionListener);
   }

   public EObject getEObject() { return _ceo; }

   public void propertyChange(java.beans.PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() { setIcon(_ceo.iconSm()); } });
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            setText(_ceo.title().toString());
            //_assistant.update();
         }
      });
   }


   private Insets _insets = new Insets(2, 5, 2, 8);
   public Insets getInsets() { return _insets; }

   public Dimension getMinimumSize() { return getPreferredSize(); }
   public Dimension getMaximumSize() { return getPreferredSize(); }
   public Dimension getPreferredSize()
   {
      Dimension d = super.getPreferredSize();
      d.width += getInsets().left + getInsets().right;
      d.width = Math.min(d.width,  MAXWIDTH);
      d.height += getInsets().top + getInsets().bottom;
      return d;
   }

   public boolean isMinimized() { return true; }

}
