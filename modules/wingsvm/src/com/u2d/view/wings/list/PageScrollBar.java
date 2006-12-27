package com.u2d.view.wings.list;

import com.u2d.list.Paginable;
import javax.swing.*;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ActionEvent;
import org.wings.SScrollBar;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 18, 2006
 * Time: 10:49:14 PM
 */
public class PageScrollBar extends SScrollBar
{
   private Paginable _paginable;

   public PageScrollBar(Paginable leo)
   {
      _paginable = leo;
      setOrientation(HORIZONTAL);
      setMinimum(1);
      setUnitIncrement(1);
      setVisibleAmount(1);

      contentsChanged();

      addAdjustmentListener(new AdjustmentListener()
      {
         public void adjustmentValueChanged(AdjustmentEvent e)
         {
            _paginable.fetchPage(e.getValue());
            setToolTipText(_paginable.getPageTitleInfo());
            postToolTipImmediately();
         }
      });
   }

   private void postToolTipImmediately()
   {
      Action action = getActionMap().get("postTip");
      if (action != null)
      {
         action.actionPerformed(new ActionEvent(com.u2d.view.wings.list.PageScrollBar.this, ActionEvent.ACTION_PERFORMED, "postTip"));
      }
   }

   public void contentsChanged()
   {
      setMaximum(_paginable.numPages()+1);
      setBlockIncrement(calcBlockIncrement());
      setToolTipText(_paginable.getPageTitleInfo());
   }

   private int calcBlockIncrement()
   {
      int blockIncrement = _paginable.numPages() / 10;
      return Math.max(1, blockIncrement);
   }

}
