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
import com.u2d.view.swing.dnd.SimpleListTransferHandler;
import com.u2d.view.swing.dnd.RelationalListDropTarget;
import com.u2d.app.Tracing;
import com.u2d.list.RelationalList;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class JListView extends SeeThruList 
                       implements SelectableListView, ListCellRenderer
{
   protected AbstractListEO _leo;
   protected ProxyListModel _leoProxy;
   
   private boolean _asIcons = false;
   private Map<Object, EView> _views = new HashMap<Object, EView>();
   
   private ChangeListener _memberChangeListener = 
      new ChangeListener()
      {
         public void stateChanged(final ChangeEvent evt)
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  int index = _leo.getItems().indexOf(evt.getSource());
                  contentsChanged(new ListDataEvent(evt.getSource(), ListDataEvent.CONTENTS_CHANGED, index, index));
               }
            });
         }
      };
   private RelationalListDropTarget _rlDropTarget;
   private SimpleListTransferHandler _transferHandler;

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
         setLayoutOrientation(VERTICAL);
      }
      else
      {
         setVisibleRowCount(Math.min(leo.getSize()+5, 15));
      }
      
      SwingViewMechanism.setupEnterKeyBinding(this);
      
      _leoProxy = new ProxyListModel(_leo);
      setModel(_leoProxy);
      setCellRenderer(this);
      
      setupTransferHandler();
      
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
      JComponent comp = componentFor(ceo);
      comp = RenderHelper.highlight(this, comp, selected, hasFocus);

      boolean odd = (index % 2) == 1;
      if (odd && !selected && !_asIcons)
      {
         Color color = ceo.type().colorCode();
         comp.setBackground(new Color(color.getRed(), color.getGreen(),
                                      color.getBlue(), 64));
      }

      return comp;
   }

   private synchronized JComponent componentFor(ComplexEObject ceo)
   {
      if (_views.get(ceo) == null)
      {
         EView view = null;
         if (_asIcons)
         {
            view = SwingViewMechanism.getInstance().getIconView(ceo);
            ((JComponent) view).setOpaque(true);
         }
         else
         {
            view = SwingViewMechanism.getInstance().getListItemViewAdapter(ceo);
         }
         // ensure that if a change takes place in an item in the list,
         // that the list gets repainted: 
         view.getEObject().addChangeListener(_memberChangeListener);

         _views.put(ceo, view);
      }
      return (JComponent) _views.get(ceo);
   }

   // JList UI Delegate already ties a listener onto model
   public void contentsChanged(ListDataEvent e)
   {
      Tracing.tracer().fine("contents changed..index0: "+e.getIndex0()
                         + "; index1: "+e.getIndex1());
      
      // force the serialization of this otherwise thorny thread-related problem..
      SwingUtilities.invokeLater(new Runnable() { public void run()
         {
            detachItems();
         } });
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
      if (_rlDropTarget != null) _rlDropTarget.detach();
      if (_transferHandler != null) _transferHandler.detach();
      firePropertyChange("model", _leo, null);  // get BasicListUI$Handler
      // to stop listening;  jprofiler tells me it still is.
   }
   
   private void detachItems()
   {
      for (Iterator itr = _views.values().iterator(); itr.hasNext(); )
      {
         EView view = (EView) itr.next();
         view.getEObject().removeChangeListener(_memberChangeListener);
         view.detach();
      }
      _views.clear();
   }
   
   public void setupTransferHandler()
   {
      setDragEnabled(true);
      _transferHandler = new SimpleListTransferHandler(this);
      setTransferHandler(_transferHandler);
      if (_leo instanceof RelationalList)
      {
         RelationalList rl = (RelationalList) _leo;
         _rlDropTarget = new RelationalListDropTarget(rl);
         setDropTarget(_rlDropTarget);
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
