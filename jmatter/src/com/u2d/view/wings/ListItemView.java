package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.view.swing.dnd.EOTransferHandler;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.element.Command;
import com.u2d.ui.UIUtils;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.*;

import org.wings.SLabel;
import org.wings.SDimension;

/**
 * @author Eitan Suez
 */
public class ListItemView extends SLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
//   private transient CommandsContextMenuView _cmdsView;
   private MouseListener _defaultActionListener;

   public ListItemView()
   {
      setHorizontalAlignment(JLabel.LEFT);
      setVerticalAlignment(JLabel.CENTER);
      setHorizontalTextPosition(JLabel.RIGHT);

//      setTransferHandler(new EOTransferHandler(this));  // set up as drag source

//      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(ComplexEObject ceo)
   {
      _ceo = ceo;
      setText(_ceo.title().toString());
//      setIcon(_ceo.iconSm());

//      setOpaque(false);

//      _cmdsView.bind(ceo, this);

      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

      Command defaultCmd = _ceo.defaultCommand();
      CommandAdapter defaultAction = new CommandAdapter(defaultCmd, _ceo, this);
      _defaultActionListener = UIUtils.doubleClickActionListener(defaultAction);
//      addMouseListener(_defaultActionListener);
   }

   public void detach()
   {
//      _cmdsView.detach();
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
//      removeMouseListener(_defaultActionListener);
      SwingViewMechanism.getInstance().returnObject(this);
   }

   public EObject getEObject() { return _ceo; }

   public void propertyChange(java.beans.PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
//         SwingUtilities.invokeLater(new Runnable() {
//            public void run() { setIcon(_ceo.iconSm()); } });
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

   public SDimension getMinimumSize() { return getPreferredSize(); }
   public SDimension getMaximumSize() { return getPreferredSize(); }
   public SDimension getPreferredSize()
   {
      SDimension d = super.getPreferredSize();
      d.setWidth(d.getWidth() + getInsets().left + getInsets().right);
      d.setWidth(Math.min(d.getWidthInt(),  MAXWIDTH));
      d.setHeight(d.getHeight() + getInsets().top + getInsets().bottom);
      return d;
   }

   public boolean isMinimized() { return true; }

}
