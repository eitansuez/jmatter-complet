package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import javax.swing.event.ListDataEvent;
import java.awt.Insets;
import javax.swing.ImageIcon;
import org.wings.*;

/**
 * @author Eitan Suez
 */
public class ListTitleView extends SLabel implements ListEView
{
   protected AbstractListEO _leo;
   private transient CommandsContextMenuView _cmdsView;

   private static SFont TITLE_FONT;
   static
   {
      TITLE_FONT = new SFont();
      TITLE_FONT.setSize(16);
      TITLE_FONT.setStyle(SFont.BOLD);
   }

   public ListTitleView(AbstractListEO leo, EView parentView)
   {
      _leo = leo;
      _leo.addChangeListener(this);
      _leo.addListDataListener(this);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_leo, this, parentView);

      setHorizontalAlignment(SConstants.LEFT);
      setVerticalAlignment(SConstants.CENTER);
      setHorizontalTextPosition(SConstants.RIGHT);
      setVerticalTextPosition(SConstants.CENTER);

      setFont(TITLE_FONT);

      setIcon(new SImageIcon((ImageIcon) _leo.iconLg()));
      updateTitle();
   }

   public void contentsChanged(ListDataEvent evt) { updateTitle(); }
   public void intervalAdded(ListDataEvent evt) { updateTitle(); }
   public void intervalRemoved(ListDataEvent evt) { updateTitle(); }
   public void stateChanged(javax.swing.event.ChangeEvent evt) { updateTitle(); }

   private void updateTitle()
   {
      setText(_leo.toString());
   }

   private Insets _insets = new Insets(2, 5, 6, 8);
   public Insets getInsets() { return _insets; }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      _cmdsView.detach();
      _leo.removeChangeListener(this);
      _leo.removeListDataListener(this);
   }

   public boolean isMinimized() { return false; }

}
