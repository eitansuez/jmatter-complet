/*
 * Created on Oct 8, 2003
 */
package com.u2d.view.swing;

import java.awt.*;
import javax.swing.*;
import java.beans.*;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * @author Eitan Suez
 */
public class TitleView extends JLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;

   public TitleView(ComplexEObject ceo, EView parentView)
   {
      super();
      _ceo = ceo;
      _ceo.addChangeListener(this);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_ceo, this, parentView);

      setFocusable(true);
      setHorizontalAlignment(JLabel.LEFT);
      setVerticalAlignment(JLabel.CENTER);
      setHorizontalTextPosition(JLabel.RIGHT);
      setVerticalTextPosition(JLabel.CENTER);
      setOpaque(false);

      ComponentStyle.addClass(this, "instance-title");
      
      setText(_ceo.viewTitle());
      setIcon(_ceo.iconLg());

      setTransferHandler(new EOTransferHandler(this));
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      if (_ceo.getState() == null) return;
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (!_ceo.isEditableState())
            {
               setText(_ceo.viewTitle());
               setIcon(_ceo.iconLg());
            }
         }
      });
   }

   public void propertyChange(PropertyChangeEvent evt) {}

   private Insets _insets = new Insets(2, 5, 6, 8);
   public Insets getInsets() { return _insets; }

   public EObject getEObject() { return _ceo; }

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

//   protected void paintBorder(java.awt.Graphics g)
//   {
//      super.paintBorder(g);
//      Graphics2D g2 = (Graphics2D) g;
//      g2.setStroke(new BasicStroke(1f));
//      int y = getLocation().y + getSize().height - 1;
//      int x1 = getLocation().x;
//      int x2 = x1 + getSize().width;
//      g2.drawLine(x1, y, x2, y);
//   }

   public boolean isMinimized() { return false; }

   public void detach()
   {
      _cmdsView.detach();
      _ceo.removeChangeListener(this);
   }

}
