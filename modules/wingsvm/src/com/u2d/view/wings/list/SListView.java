package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.Selectable;
import com.u2d.view.EView;
import com.u2d.view.wings.WingSViewMechanism;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.app.Context;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.ListSelectionModel;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.*;
import org.wings.SList;
import org.wings.SListCellRenderer;
import org.wings.SComponent;

/**
 * @author Eitan Suez
 */
public class SListView extends SList
                       implements ListEView, SListCellRenderer, Selectable
{
   protected AbstractListEO _leo;
   private boolean _asIcons = false;
   private Map _views = new HashMap();

   private ChangeListener _memberChangeListener =
      new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            int index = _leo.getItems().indexOf(evt.getSource());
            contentsChanged(new ListDataEvent(evt.getSource(), ListDataEvent.CONTENTS_CHANGED, index, index));
         }
      };

   public SListView(AbstractListEO leo)
   {
      this(leo, false);
   }
   public SListView(AbstractListEO leo, boolean renderCellsAsIcons)
   {
      _leo = leo;

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      _asIcons = renderCellsAsIcons;
      if (_asIcons)
      {
         setVisibleRowCount(1);
//         setLayoutOrientation(HORIZONTAL_WRAP);
      }
      else
      {
         setVisibleRowCount(Math.min(leo.getSize()+5, 15));
      }

//      SwingViewMechanism.setupEnterKeyBinding(this);

      setModel(_leo);
      setCellRenderer(this);

//      setDragEnabled(true);
//      setTransferHandler(new SListView.SimpleListTransferHandler());

      _leo.addListDataListener(this);

      selectFirst();
   }

   private void selectFirst()
   {
      if (_leo.isEmpty()) return;
      getSelectionModel().setSelectionInterval(0, 0);
   }

   private WingSViewMechanism vmech()
   {
      return (WingSViewMechanism) Context.getInstance().getViewMechanism();
   }

   public SComponent getListCellRendererComponent(SComponent list,
                                                  Object value,
                                                  boolean selected,
                                                  int index)
   {
      ComplexEObject ceo = (ComplexEObject) value;

      if (_views.get(ceo) == null)
      {
         EView view = null;
         if (_asIcons)
         {
            view = vmech().getIconView(ceo);
         }
         else
         {
            view = vmech().getListItemView(ceo);

            // ensure that if a change takes place in an item in the list,
            // that the list gets repainted: 
            view.getEObject().addChangeListener(_memberChangeListener);
         }

         _views.put(ceo, view);
      }
      SComponent comp = (SComponent) _views.get(ceo);

      comp.setBackground( selected ? ((SList) list).getSelectionBackground() : list.getBackground() );
      comp.setForeground( selected ? ((SList) list).getSelectionForeground() : list.getForeground() );
      
      boolean odd = (index % 2) == 1;
      if (odd && !selected)
      {
         Color color = ceo.type().colorCode();
         comp.setBackground(new Color(color.getRed(), color.getGreen(),
                                      color.getBlue(), 64));
      }

      return comp;
   }

   // JList UI Delegate already ties a listener onto model
   public void contentsChanged(ListDataEvent e)
   {
      detachItems();  // dec 7 2005: ??? why did i do this?
   }

   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }

   public void stateChanged(ChangeEvent evt)
   {
      // noop
   }

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      _leo.removeListDataListener(this);
      detachItems();
//      firePropertyChange("model", _leo, null);  // get BasicListUI$Handler
      // to stop listening;  jprofiler tells me it still is.
   }
   private void detachItems()
   {
      EView view = null;
      for (Iterator itr = _views.values().iterator(); itr.hasNext(); )
      {
         view = (EView) itr.next();
         view.getEObject().removeChangeListener(_memberChangeListener);
         view.detach();
      }
      _views.clear();
   }

//   class SimpleListTransferHandler extends TransferHandler
//   {
//      protected Transferable createTransferable(JComponent source)
//      {
//         return (ComplexEObject) getSelectedValue();
//      }
//      public int getSourceActions(JComponent c) { return COPY_OR_MOVE; }
//      protected void exportDone(JComponent c, Transferable t, int action)
//      {
//         // noop
//      }
//   }

//   public Dimension getPreferredScrollableViewportSize()
//   {
//      Dimension preferred = super.getPreferredScrollableViewportSize();
//      preferred.height = Math.max(preferred.height, MINHEIGHT);
//      preferred.width = Math.min(preferred.width, MAXWIDTH);
//      return preferred;
//   }

   public ComplexEObject selectedEO() { return (ComplexEObject) getSelectedValue(); }

   public boolean isMinimized() { return false; }

}
