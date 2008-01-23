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
import com.u2d.app.Tracing;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class JListSelectionView
      extends JList implements SelectableListView, ListCellRenderer
{
   protected AbstractListEO _leo;
   protected ProxyListModel _leoProxy;
   
   private Map<Object, EView> _views = new HashMap<Object, EView>();
   
   public JListSelectionView(AbstractListEO leo)
   {
      _leo = leo;
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      setVisibleRowCount(leo.getSize());
      
      _leoProxy = new ProxyListModel(_leo);
      setModel(_leoProxy);
      setCellRenderer(this);
      
      _leo.addListDataListener(this);
      
      selectFirst();
   }

   private void selectFirst()
   {
      if (_leo.isEmpty()) return;
      getSelectionModel().setSelectionInterval(0, 0);
   }


   public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean selected, boolean hasFocus)
   {
      ComplexEObject ceo = (ComplexEObject) value;
      JComponent comp = componentFor(ceo);
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

   private JComponent componentFor(ComplexEObject ceo)
   {
      if (_views.get(ceo) == null)
      {
         EView view = SwingViewMechanism.getInstance().getListItemViewAdapter(ceo);
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
      firePropertyChange("model", _leo, null);  // get BasicListUI$Handler
      // to stop listening;  jprofiler tells me it still is.
   }
   
   private void detachItems()
   {
      _views.clear();
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