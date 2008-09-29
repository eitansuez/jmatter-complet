/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.DropTargetHandler;

/**
 * @author Eitan Suez
 */
public class ListItemView extends JLabel implements ListEView
{
   protected AbstractListEO _leo;
   private transient CommandsContextMenuView _cmdsView;

   public ListItemView(AbstractListEO leo, Insets insets)
   {
      this(leo);
      _insets = insets;
   }

   public ListItemView(AbstractListEO leo)
   {
      init(leo);
      _leo.addListDataListener(this);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(leo, this);

      // allow drop to add item to list..
      setTransferHandler(new DropTargetHandler());
   }

   private void init(AbstractListEO leo)
   {
      _leo = leo;

      setHorizontalAlignment(JLabel.LEADING);
      setVerticalAlignment(JLabel.CENTER);
      setHorizontalTextPosition(JLabel.TRAILING);

      setText(_leo.title().toString());
      setIcon(_leo.iconSm());

      setOpaque(false);
   }

   public EObject getEObject() { return _leo; }

   private Insets _insets = new Insets(2, 5, 2, 8);
   public Insets getInsets() { return _insets; }

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
      _leo.removeListDataListener(this);
      setTransferHandler(null);
   }

   public void contentsChanged(ListDataEvent e) { updateText(); }
   public void intervalAdded(ListDataEvent e) { updateText(); }
   public void intervalRemoved(ListDataEvent e) { updateText(); }
   private void updateText()
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            setText(_leo.toString());
         } });
   }


   public void stateChanged(ChangeEvent e) {}

   public boolean isMinimized() { return true; }

}
