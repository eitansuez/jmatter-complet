package com.u2d.view.swing.list;

import com.u2d.ui.RenderHelper;
import com.u2d.ui.GridList;
import com.u2d.view.EView;
import com.u2d.view.SelectableListView;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.view.swing.dnd.SimpleListTransferHandler;
import com.u2d.view.swing.dnd.RelationalListDropTarget;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.list.RelationalList;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Eitan Suez
 */
public class GridListView
      extends GridList
      implements SelectableListView, ListCellRenderer
{
   protected AbstractListEO _leo;
   protected ProxyListModel _leoProxy;
   
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
   private RelationalListDropTarget _rlDropTarget;
   private SimpleListTransferHandler _transferHandler;

   public GridListView(AbstractListEO leo)
   {
      super();
      _leo = leo;
      
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      setupAsSeeThru();
      SwingViewMechanism.setupEnterKeyBinding(this);
      
      _leoProxy = new ProxyListModel(_leo);
      setModel(_leoProxy);
      setCellRenderer(this);
      
      setupTransferHandler();
      
      _leo.addListDataListener(this);
      
      selectFirst();
      applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
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
         EView view;
         
         view = SwingViewMechanism.getInstance().getIconView(ceo);
         ((JComponent) view).setOpaque(true);

         // ensure that if a change takes place in an item in the list,
         // that the list gets repainted: 
         view.getEObject().addChangeListener(_memberChangeListener);

         _views.put(value, view);
      }
      JComponent comp = (JComponent) _views.get(value);
      comp = RenderHelper.highlight(this, comp, selected, hasFocus);

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
      _leoProxy.detach();
      detachItems();
      if (_rlDropTarget != null) _rlDropTarget.detach();
      if (_transferHandler != null) _transferHandler.detach();
      firePropertyChange("model", _leo, null);  // get BasicListUI$Handler
      // to stop listening;  jprofiler tells me it still is.
   }
   private void detachItems()
   {
      EView view;
      for (Iterator itr = _views.values().iterator(); itr.hasNext(); )
      {
         view = (EView) itr.next();
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


   // == to enable right-clicking on individual items in list.. (taken from seethrulist..)
   private void setupAsSeeThru()
   {
      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            if (evt.isPopupTrigger())
               dispatch(evt);
         }
         // for microsoft platform:
         public void mouseReleased(MouseEvent evt)
         {
            if (evt.isPopupTrigger())
               dispatch(evt);
         }
         public void mouseClicked(MouseEvent evt)
         {
            dispatch(evt);
         }
      });
		
      addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent evt)
         {
            dispatch(evt);
         }
      });
   }

   private void dispatch(MouseEvent evt)
   {
      int index = locationToIndex(new Point(evt.getX(), evt.getY()));
      if (index < 0) return;
      renderedComponent(index).dispatchEvent(evt);
   }
   
}
