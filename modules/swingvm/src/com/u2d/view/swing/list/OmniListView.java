/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing.list;

import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.view.swing.LTRCapableJSplitPane;
import com.u2d.ui.CardBuffer;

/**
 * @author Eitan Suez
 */
public class OmniListView extends LTRCapableJSplitPane
                          implements ListEView, ListSelectionListener, CompositeView
{
   private AbstractListEO _leo;
   private JListView _list;

   private JPanel _blankPanel = new JPanel() {
      public Dimension getMinimumSize() { return new Dimension(400,200); }
      public Dimension getPreferredSize() { return getMinimumSize(); }
   };

   static int LEFT_COMPONENT = 1;
   static int RIGHT_COMPONENT = 2;

   CardBuffer _cardBuffer;

   public OmniListView(AbstractListEO leo)
   {
      _leo = leo;
      applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

      setOrientation(HORIZONTAL_SPLIT);
      setDividerSize(8);
      setOneTouchExpandable(true);
      setResizeWeight(0.3);
      
      _list = new JListView(_leo);
      _list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      JScrollPane scrollPane = new JScrollPane(_list);
      setLeftComponent(scrollPane);

      _cardBuffer = new CardBuffer(_blankPanel);
      setRightComponent(_cardBuffer);

      _list.getSelectionModel().addListSelectionListener(this);
      if (_list.getSelectedIndex() == 0)
      {
         switchInView();
      }
      else
      {
         _list.setSelectedIndex(0);
      }
   }
   
   public void valueChanged(ListSelectionEvent evt)
   {
      if (evt.getValueIsAdjusting()) return;
      switchInView();
   }

   private void switchInView()
   {
      ComplexEObject ceo = (ComplexEObject) _list.getSelectedValue();
      Component previous = null;

      try
      {
         if (ceo == null)
         {
            previous = _cardBuffer.switchIn(_blankPanel);
         }
         else
         {
            previous = _cardBuffer.switchIn((JComponent) ceo.getMainView());
         }
      }
      finally
      {
         if (previous != null && previous instanceof EView)
         {
            ((EView) previous).detach();
         }
      }
   }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      _list.detach();
      Component[] items = _cardBuffer.getItems();
      for (Component item : items)
      {
         if (item != null && item instanceof EView)
         {
            ((EView) item).detach();
         }
      }
   }

   public Dimension getMinimumSize()
   {
      return new Dimension(550,300);
   }
   public Dimension getPreferredSize()
   {
      Dimension preferred = super.getPreferredSize();
      Dimension minimum = getMinimumSize();
      if (preferred.width < minimum.width)
      {
         return minimum;
      }
      return preferred;
   }
   
   public void contentsChanged(ListDataEvent e) {}
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}
   public void stateChanged(ChangeEvent e) {}
   
   public EView getInnerView() { return _list; }

   public boolean isMinimized() { return false; }

}
