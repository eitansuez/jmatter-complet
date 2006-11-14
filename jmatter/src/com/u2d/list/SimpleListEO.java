/*
 * Created on Jan 29, 2004
 */
package com.u2d.list;

import java.util.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class SimpleListEO extends AbstractListEO
{
   protected ComplexType _itemType;
   protected Class _clazz;

   public SimpleListEO() {}

   public SimpleListEO(Class clazz)
   {
      _clazz = clazz;
      _items = new ArrayList();
   }

   public SimpleListEO(Class clazz, List items)
   {
      this(clazz);
      setItems(items);
   }

   public SimpleListEO(ComplexType itemType)
   {
      _itemType = itemType;
      _items = new ArrayList();
   }

   public SimpleListEO(ComplexType type, List items)
   {
      this(type);
      setItems(items);
   }

   // lazy derivation of type from class avoids infinite recursion when harvesting if
   // this were placed eagerly in the constructor
   public ComplexType type()
   {
      if (_itemType == null)
         _itemType = ComplexType.forClass(_clazz);
      return _itemType;
   }

   public Class getJavaClass() { return _clazz; }

   public boolean isEmpty() { return _items.isEmpty(); }
   public int getSize() { return _items.size(); }
   public int getTotal() { return getSize(); }

   public EObject makeCopy()
   {
      return new SimpleListEO(_itemType, _items);
   }


   /* ** ===== View-Related ===== ** */

   public EView getView() { return getAlternateView(); }
   public EView getMainView() { return getView(); }
   
   public ListEView getListView() { return vmech().getListView(this); }
   public ListEView getListViewAsIcons() { return vmech().getListViewAsIcons(this); }
   public ListEView getToolbarView(String name) { return vmech().getToolbarView(name, this); }
   public ListEView getListViewAsTree() { return vmech().getListViewAsTree(this); }
   public ListEView getTableView() { return vmech().getListViewAsTable(this); }

   public ListEView getAlternateView()
   {
      return vmech().getAlternateListView(this,
                                          new String[]
            {"listiconsview", "listview", "listtableview", "omnilistview"});
   }


}
