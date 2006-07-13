package com.u2d.view.swing.list;

import com.u2d.list.Paginable;
import com.u2d.ui.IconButton;
import com.u2d.view.swing.calendar.NavPanel;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 18, 2006
 * Time: 10:49:14 PM
 */
public class PageScrollBar extends JScrollBar
{
   private Paginable _paginable;
   private JButton _prevBtn = new IconButton(NavPanel.PREV_ICON, NavPanel.PREV_ROLLOVER);
   private JButton _nextBtn = new IconButton(NavPanel.NEXT_ICON, NavPanel.NEXT_ROLLOVER);

   public PageScrollBar(Paginable leo)
   {
      _paginable = leo;
      setOrientation(HORIZONTAL);
      setMinimum(1);
      setUnitIncrement(1);
      setVisibleAmount(1);

      setUI(new BasicScrollBarUI()
      {
         protected JButton createIncreaseButton(int orientation)
         {
            return _nextBtn;
         }

         protected JButton createDecreaseButton(int orientation)
         {
            return _prevBtn;
         }
      });

      contentsChanged();
      setupToDisplayTooltipOnAdjustment();
   }

   /*
    * See 
    */
   private void setupToDisplayTooltipOnAdjustment()
   {
      InputMap imap = getInputMap();
      //put dummy KeyStroke into InputMap if is empty:
      boolean removeKeyStroke = false;
      KeyStroke[] ks = imap.keys();
      if (ks == null || ks.length == 0)
      {
         imap.put(KeyStroke.getKeyStroke(
               KeyEvent.VK_BACK_SLASH, 0), "backSlash");
         removeKeyStroke = true;
      }
      //now we can register by ToolTipManager
      ToolTipManager.sharedInstance().registerComponent(this);
      //and remove dummy KeyStroke
      if (removeKeyStroke)
      {
         imap.remove(KeyStroke.getKeyStroke(
               KeyEvent.VK_BACK_SLASH, 0));
      }
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
         action.actionPerformed(new ActionEvent(PageScrollBar.this, ActionEvent.ACTION_PERFORMED, "postTip"));
      }
   }

   public Dimension getPreferredSize()
   {
      Dimension dim = super.getPreferredSize();
      int height = Math.max(dim.height,  _nextBtn.getPreferredSize().height);
      if (_paginable.numPages() == 1) height = 0;
      return new Dimension(dim.width, height);
   }

   public void contentsChanged()
   {
      setMaximum(_paginable.numPages()+1);
      setBlockIncrement(calcBlockIncrement());
      setToolTipText(_paginable.getPageTitleInfo());
      
      if ( (_paginable.numPages() == 1) && (_nextBtn.isVisible()) )
      {
         _nextBtn.setVisible(false);
         _prevBtn.setVisible(false);
      }
      else if ( (_paginable.numPages() > 1) && (!_nextBtn.isVisible()) )
      {
         _nextBtn.setVisible(true);
         _prevBtn.setVisible(true);
      }
   }

   private int calcBlockIncrement()
   {
      int blockIncrement = _paginable.numPages() / 10;
      return Math.max(1, blockIncrement);
   }

}
