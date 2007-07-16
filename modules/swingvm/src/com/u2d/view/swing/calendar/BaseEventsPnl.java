package com.u2d.view.swing.calendar;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

/**
 * @author Eitan Suez
 */
public abstract class BaseEventsPnl
      extends JPanel
      implements AdjustmentListener, ListDataListener,
      TableColumnModelListener, ChangeListener
{
   protected TimeIntervalView _view;
   private java.awt.LayoutManager _layout;
   
   public BaseEventsPnl(TimeIntervalView view)
   {
      _view = view;

      _layout = new PositionedLayout(_view);
      setLayout(_layout);
      setOpaque(false);

      _view.addAdjustmentListener(this);
      _view.getSpan().addChangeListener(this);
   }

   public void stateChanged(ChangeEvent e) { updateView(); }

   public void intervalAdded(ListDataEvent e) { updateView(); }
   public void intervalRemoved(ListDataEvent e) { updateView(); }
   public void contentsChanged(ListDataEvent e) { updateView(); }

   // since the scrollbar is tied to the weekview, need to do some work
   // to ensure that this panel is also driven by it
   public void adjustmentValueChanged(AdjustmentEvent evt)
   {
      _layout.layoutContainer(this);
   }

   // implementation of tablecolumnmodellistener
   public void columnMoved(TableColumnModelEvent evt)
   {
      _layout.layoutContainer(this);
   }
   public void columnMarginChanged(ChangeEvent evt)
   {
      _layout.layoutContainer(this);
   }
   public void columnAdded(TableColumnModelEvent evt) { }
   public void columnRemoved(TableColumnModelEvent evt) { }
   public void columnSelectionChanged(ListSelectionEvent evt) { }

   public abstract void updateView();
   public abstract void detach();

}
