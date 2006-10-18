package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.ImageIcon;
import org.wings.*;

/**
 * @author Eitan Suez
 */
public class ListItemView
      extends SLabel
      implements ListEView
{
   protected AbstractListEO _leo;
   private transient CommandsContextMenuView _cmdsView;

   public ListItemView(AbstractListEO leo)
   {
      init(leo);
      _leo.addListDataListener(this);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(leo, this);

      // allow drop to add item to list..
//      setTransferHandler(new DropTargetHandler());
   }

   private void init(AbstractListEO leo)
   {
      _leo = leo;

      setHorizontalAlignment(SConstants.LEFT);
      setVerticalAlignment(SConstants.CENTER);
      setHorizontalTextPosition(SConstants.RIGHT);

      setText(_leo.title().toString());
      setIcon(new SImageIcon((ImageIcon) _leo.iconSm()));
   }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      _cmdsView.detach();
      _leo.removeListDataListener(this);
   }

   public void contentsChanged(ListDataEvent e) { updateText(); }
   public void intervalAdded(ListDataEvent e) { updateText(); }
   public void intervalRemoved(ListDataEvent e) { updateText(); }
   private void updateText() { setText(_leo.toString()); }

   public void stateChanged(ChangeEvent e) {}
   public boolean isMinimized() { return true; }

}
