package com.u2d.view.echo.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.echo.ListItemView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import nextapp.echo.app.ListBox;
import nextapp.echo.app.Component;
import nextapp.echo.app.list.AbstractListModel;
import nextapp.echo.app.list.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 5, 2008
 * Time: 11:48:42 AM
 */
public class ListView extends ListBox implements ListEView
{
   private AbstractListEO _leo;
   private Map<ComplexEObject, EView> _views = new HashMap<ComplexEObject, EView>();
   
   public ListView(AbstractListEO leo)
   {
      _leo = leo;
      setModel(new AbstractListModel()
      {
         public Object get(int index)
         {
            return _leo.get(index);
         }

         public int size()
         {
            return _leo.getSize();
         }
      });
      setCellRenderer(new ListCellRenderer()
      {
         public Object getListCellRendererComponent(Component list, Object value, int index)
         {
            ComplexEObject eo = (ComplexEObject) value;
            if (_views.containsKey(eo))
            {
               ListItemView view = (ListItemView) _views.get(eo);
               return view;
            }
            ListItemView view = new ListItemView(eo);
            _views.put(eo, view);
            return view;
         }
      });
   }

   public EObject getEObject()
   {
      return _leo;
   }

   public void detach()
   {
      for (EView view : _views.values())
      {
         view.detach();
      }
      _views.clear();
   }

   public boolean isMinimized() { return false; }

   // tbd
   public void stateChanged(ChangeEvent e)
   {
   }

   public void intervalAdded(ListDataEvent e)
   {
   }

   public void intervalRemoved(ListDataEvent e)
   {
   }

   public void contentsChanged(ListDataEvent e)
   {
   }
}
