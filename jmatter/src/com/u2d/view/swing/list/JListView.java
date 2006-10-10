/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.*;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * @author Eitan Suez
 */
public class JListView extends SeeThruList 
                       implements ListEView, ListCellRenderer, Selectable
{
   protected AbstractListEO _leo;
   protected ProxyListModel _leoProxy;
   
   private boolean _asIcons = false;
   private Map<Object, EView> _views = new HashMap<Object, EView>();
   
   private ChangeListener _memberChangeListener = 
      new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            int index = _leo.getItems().indexOf(evt.getSource());
            contentsChanged(new ListDataEvent(evt.getSource(), ListDataEvent.CONTENTS_CHANGED, index, index));
         }
      };
   
   public JListView(AbstractListEO leo)
   {
      this(leo, false);
   }
   public JListView(AbstractListEO leo, boolean renderCellsAsIcons)
   {
      _leo = leo;
      
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      _asIcons = renderCellsAsIcons;
      if (_asIcons)
      {
         setVisibleRowCount(1);
         setLayoutOrientation(HORIZONTAL_WRAP);
      }
      else
      {
         setVisibleRowCount(Math.min(leo.getSize()+5, 15));
      }
      
      SwingViewMechanism.setupEnterKeyBinding(this);
      
      _leoProxy = new ProxyListModel(_leo);
      setModel(_leoProxy);
      setCellRenderer(this);
      
      setDragEnabled(true);
      setTransferHandler(new SimpleListTransferHandler());
      
      _leo.addListDataListener(this);
      
      selectFirst();
   }

   private void selectFirst()
   {
      if (_leo.isEmpty()) return;
      getSelectionModel().setSelectionInterval(0, 0);
   }


   public java.awt.Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean selected, boolean hasFocus)
   {
      ComplexEObject ceo = (ComplexEObject) value;

      if (_views.get(value) == null)
      {
         EView view = null;
         if (_asIcons)
         {
            view = SwingViewMechanism.getInstance().
                  getIconViewAdapter(ceo);
         }
         else
         {
            view = SwingViewMechanism.getInstance().
                  getListItemViewAdapter(ceo);

            // ensure that if a change takes place in an item in the list,
            // that the list gets repainted: 
            view.getEObject().addChangeListener(_memberChangeListener);
         }

         _views.put(value, view);
      }
      JComponent comp = (JComponent) _views.get(value);

      comp = RenderHelper.highlight(this, comp, selected, hasFocus);

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
   
   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      // noop
   }
   
   public EObject getEObject() { return _leo; }
   
   public void detach()
   {
      _leo.removeListDataListener(this);
      _leoProxy.detach();
      detachItems();
      firePropertyChange("model", _leo, null);  // get BasicListUI$Handler
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
   
   class SimpleListTransferHandler extends TransferHandler
   {
      protected Transferable createTransferable(JComponent source)
      {
         return (ComplexEObject) getSelectedValue();
      }
      public int getSourceActions(JComponent c) { return COPY_OR_MOVE; }
      protected void exportDone(JComponent c, Transferable t, int action)
      {
         // noop
      }
   }
   
   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension preferred = super.getPreferredScrollableViewportSize();
      preferred.height = Math.max(preferred.height, MINHEIGHT);
      preferred.width = Math.min(preferred.width, MAXWIDTH);
      return preferred;
   }
   
   public ComplexEObject selectedEO() { return (ComplexEObject) getSelectedValue(); }

   public boolean isMinimized() { return false; }

}
