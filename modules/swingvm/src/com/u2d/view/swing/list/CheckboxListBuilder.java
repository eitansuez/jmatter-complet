package com.u2d.view.swing.list;

import com.u2d.ui.RenderHelper;
import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.list.SimpleListEO;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Date: Jun 15, 2005
 * Time: 4:11:49 PM
 *
 * @author Eitan Suez
 */
public class CheckboxListBuilder extends JList
      implements ListEView, ListCellRenderer
{
   protected AbstractListEO _subset;
   protected AbstractListEO _superset;
   private Map _views = new HashMap();

   protected CheckboxListBuilder() {}

   public CheckboxListBuilder(AbstractListEO superset, AbstractListEO subset)
   {
      this(superset, subset, 2);
   }

   public CheckboxListBuilder(AbstractListEO superset, AbstractListEO subset, int numCols)
   {
      _subset = subset;
      init(superset, numCols);
   }

   protected void init(AbstractListEO superset, int numCols)
   {
      _superset = superset;
      _subset = new SimpleListEO();
      setupLayout(superset.getSize(), numCols);
      setModel(_superset);
      setCellRenderer(this);

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

   }

   private void setupLayout(int numItems, int numCols)
   {
      setLayoutOrientation(JList.VERTICAL_WRAP);
      boolean evenDivision = (numItems % numCols) == 0;
      int rowCount = numItems / numCols + (evenDivision ? 0 : 1);
      setVisibleRowCount(rowCount);
   }

   public java.awt.Component getListCellRendererComponent(JList list, Object value,
         int index, boolean selected, boolean hasFocus)
   {
      ComplexEObject ceo = (ComplexEObject) value;

      if (_views.get(value) == null)
      {
         EView view = new CheckBoxView(ceo);
         _views.put(value, view);
      }
      JComponent comp = (JComponent) _views.get(value);
      CheckBoxView cbview = (CheckBoxView) comp;
      cbview.setSelected(_subset.contains(ceo));

      comp = RenderHelper.highlight(this, comp, selected, hasFocus);

      boolean odd = (index % 2) == 1;
      if (odd && !hasFocus)
      {
         Color color = ceo.type().colorCode();
         comp.setBackground(new Color(color.getRed(), color.getGreen(),
               color.getBlue(), 128));
      }

      return comp;
   }

   public void setSelections(AbstractListEO selections)
   {
      if (selections == null)
         _subset = new SimpleListEO();
      else
         _subset = selections;
      repaint();
   }

   public void contentsChanged(ListDataEvent e) {}
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   public EObject getEObject() { return _subset; }

   public void detach()
   {
      for (Iterator itr = _views.values().iterator(); itr.hasNext(); )
      {
         ((EView) itr.next()).detach();
      }
   }

   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension preferred = super.getPreferredScrollableViewportSize();
      preferred.height = Math.max(preferred.height, MINHEIGHT);
      return preferred;
   }


   class CheckBoxView extends JPanel implements EView
   {
      private ComplexEObject _ceo;
      private EView _listItemView;
      private JCheckBox _checkbox;
      private Insets insets = new Insets(0, 0, 0, 0);

      public CheckBoxView(ComplexEObject ceo)
      {
         _ceo = ceo;

         _listItemView = SwingViewMechanism.getInstance().getListItemViewAdapter(ceo);
         ((JComponent) _listItemView).setOpaque(false);

         _checkbox = new JCheckBox();
         _checkbox.setOpaque(false);

         setLayout(new FlowLayout(FlowLayout.LEADING));

         add(_checkbox);
         add((JComponent) _listItemView);

         setupDispatchMouseEvents();
      }

      public Insets getInsets() { return insets; }

      public void setSelected(boolean selected) { _checkbox.setSelected(selected); }

      public EObject getEObject() { return _ceo; }
      public void detach() { _listItemView.detach(); }
      public void stateChanged(ChangeEvent e) {}

      private void setupDispatchMouseEvents()
      {
         addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent evt)
            {
               int x = evt.getX();  int y = evt.getY();
               Component c = getComponentAtRelative(x, y);
               if (c != null)
               {
                  if (c instanceof JCheckBox)
                  {
                     if (_subset.contains(_ceo))
                     {
                        _subset.remove(_ceo);
                     }
                     else
                     {
                        _subset.add(_ceo);
                     }
                     evt.getComponent().repaint();
                  }

                  c.dispatchEvent(evt);
               }
            }
         });
      }

      private Component getComponentAtRelative(int x, int y)
      {
         int x1, y1, x2, y2;
         Component c;
         for (int i=0; i<getComponentCount(); i++)
         {
            c = getComponent(i);
            x1 = c.getLocation().x;
            x2 = x1 + c.getWidth();
            y1 = c.getLocation().y;
            y2 = y1 + c.getHeight();
            if (inrange(x, x1, x2) && inrange(y, y1, y2))
            {
               return c;
            }
         }
         return null;
      }
      private boolean inrange(int n, int n1, int n2)
      {
         return ( n > n1 ) && ( n < n2 );
      }
   }


   private void dispatch(MouseEvent evt)
   {
      int index = locationToIndex(new Point(evt.getX(), evt.getY()));
      if (index < 0) return;

      setSelectedIndex(index);

      Object value = getModel().getElementAt(index);
      ListCellRenderer renderer = getCellRenderer();
      Component item = renderer.getListCellRendererComponent(CheckboxListBuilder.this,
            value, index, false, false);

      Point origin = indexToLocation(index);
      evt.translatePoint(-origin.x,  -origin.y);
      item.dispatchEvent(evt);
   }

   public boolean isMinimized() { return false; }

}
