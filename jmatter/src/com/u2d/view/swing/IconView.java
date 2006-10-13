/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import java.beans.*;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.NullComplexEObject;
import com.u2d.ui.Caption;
import com.u2d.ui.UIUtils;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.element.Command;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

/**
 * @author Eitan Suez
 */
public class IconView extends Caption implements ComplexEView
{
   protected ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;
   private MouseListener _defaultActionListener;

   public IconView()
   {
      setHorizontalAlignment(SwingConstants.CENTER);
      setVerticalAlignment(SwingConstants.TOP);
      setHorizontalTextPosition(SwingConstants.CENTER);
      setVerticalTextPosition(SwingConstants.BOTTOM);
      setAlignmentX(0.5f);
      setAlignmentY(0.5f);
      setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
      setOpaque(false);
      setBreakPosition(15);

      // TODO:  assign fonts and colors from preferences
      Font font = getFont().deriveFont(Font.PLAIN, 10.0f);
      setFont(font);

      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(ComplexEObject ceo)
   {
      _ceo = ceo;
      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

      _cmdsView.bind(ceo, this);

      setText(_ceo.title().toString());
      setIcon(_ceo.iconLg());

      // TODO: NullComplexEObject should have its own iconview.
      //  i shouldn't have to put conditions in like this..
      if (_ceo instanceof NullComplexEObject)
      {
         setTransferHandler(new DropTargetHandler());
      }
      else
      {
         setTransferHandler(new EOTransferHandler(this));  // setup as drag source
      }

      Command defaultCmd = _ceo.defaultCommand();
      CommandAdapter defaultAction = new CommandAdapter(defaultCmd, _ceo, this);
      _defaultActionListener = UIUtils.doubleClickActionListener(defaultAction);
      addMouseListener(_defaultActionListener);
   }

   public void detach()
   {
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);

      _cmdsView.detach();
      
      removeMouseListener(_defaultActionListener);
   }

   public EObject getEObject() { return _ceo; }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         setIcon(_ceo.iconLg());
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               setText(_ceo.title().toString());
            }
         });
   }

   private Insets _insets = new Insets(3, 4, 3, 4);
   public Insets getInsets() { return _insets; }

   public Dimension getMinimumSize()
   {
      return getPreferredSize();
   }
   public Dimension getMaximumSize()
   {
      return getPreferredSize();
   }
   public Dimension getPreferredSize()
   {
      Dimension d = super.getPreferredSize();
      d.width += getInsets().left + getInsets().right;
      d.height += getInsets().top + getInsets().bottom;
      return d;
   }

   public boolean isMinimized() { return true; }

}
