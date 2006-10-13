/*
 * Created on Mar 31, 2005
 */
package com.u2d.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.u2d.app.ViewMechanism;
import com.u2d.app.Context;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;

/**
 * @author Eitan Suez
 */
public class ViewInfo
{
   Icon _icon, _rolloverIcon;
   String _factoryName;

   public ViewInfo(String viewname, String factoryName)
   {
      this(viewname+".png", viewname+"-hover.png", factoryName);
   }
   
   public ViewInfo(Icon icon, Icon rolloverIcon, String factoryName)
   {
      _icon = icon;
      _rolloverIcon = rolloverIcon;
      _factoryName = factoryName;
   }
   
   public ViewInfo(String iconName, String rolloverIconName, String factoryName)
   {
      ClassLoader loader = getClass().getClassLoader();
      
      URL url = loader.getResource("images/" + iconName);
      _icon = new ImageIcon(url);
      url = loader.getResource("images/" + rolloverIconName);
      _rolloverIcon = new ImageIcon(url);
      
      _factoryName = factoryName;
   }
   
   public ListEView getListView(AbstractListEO leo) throws NoSuchMethodException, 
            InvocationTargetException, IllegalAccessException
   {
      ViewMechanism vmech = Context.getInstance().getViewMechanism();
      
      Class[] paramTypes = {AbstractListEO.class};
      Method factory = vmech.getClass().getMethod(_factoryName, paramTypes);

      return (ListEView) factory.invoke(vmech, new Object[] {leo});
   }
   
   public ComplexEView getView(ComplexEObject ceo) throws NoSuchMethodException,
         InvocationTargetException, IllegalAccessException
   {
      ViewMechanism vmech = Context.getInstance().getViewMechanism();
      
      Class[] paramTypes = {ComplexEObject.class};
      Method factory = vmech.getClass().getMethod(_factoryName, paramTypes);
      return (ComplexEView) factory.invoke(vmech, new Object[] {ceo});
   }
   
   public Icon getIcon() { return _icon; }
   public Icon getRolloverIcon() { return _rolloverIcon; }
   
   
   // ====
   
   static Map<String, ViewInfo> _listViewMap = new HashMap<String, ViewInfo>();
   static
   {
      _listViewMap.put("listtableview", 
            new ViewInfo("listtableview", "getListViewAsTable"));
      _listViewMap.put("listview",
            new ViewInfo("listview", "getListView"));
      _listViewMap.put("listiconsview",
            new ViewInfo("listiconsview", "getListViewAsIcons"));
      _listViewMap.put("listtreeview",
            new ViewInfo("listtreeview", "getListViewAsTree"));
      _listViewMap.put("omnilistview",
            new ViewInfo("omnilistview", "getOmniListView"));
   }
   
   
   /**
    * an attempt to setup a mechanism whereby i can specify which view
    * i want without the cost of instantiating it.  that is, associating
    * a view name to a view factory method name..for later resolution.
    * 
    * this is directly tied to the implementation of alternateview
    * which lazily instantiates views upon request (although view list
    * is specified a priori via view name).
    */
   public static ListEView getListViewByName(String viewName, AbstractListEO leo)
   {
      ViewInfo viewInfo = (ViewInfo) _listViewMap.get(viewName);
      try
      {
         return viewInfo.getListView(leo);
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
      }
      return null;
   }
   
   public static ViewInfo getListViewInfo(String viewName)
   {
      return (ViewInfo) _listViewMap.get(viewName);
   }

   
   
   // that's a lot of duplication..
   
   static Map<String, ViewInfo> _viewMap = new HashMap<String, ViewInfo>();
   static
   {
      _viewMap.put("formview",
            new ViewInfo("formview", "getFormView"));
      _viewMap.put("folderview",
            new ViewInfo("folderview", "getFolderView"));
      _viewMap.put("outlookview",
            new ViewInfo("folderview.png", "folderview-hover.png", "getOutlookView"));
      _viewMap.put("treeview",
            new ViewInfo("treeview", "getTreeView"));
      _viewMap.put("omniview",
            new ViewInfo("omniview", "getOmniView"));
      _viewMap.put("iconview",
            new ViewInfo("listiconsview.png", "listiconsview-hover.png", "getIconView"));
      _viewMap.put("collapsedview",
            new ViewInfo("listiconsview.png", "listiconsview-hover.png", "getCollapsedView"));
   }
   
   public static ComplexEView getViewByName(String viewName, ComplexEObject ceo)
   {
      ViewInfo viewInfo = (ViewInfo) _viewMap.get(viewName);
      try
      {
         return viewInfo.getView(ceo);
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
      }
      return null;
   }
   
   public static ViewInfo getViewInfo(String viewName)
   {
      return (ViewInfo) _viewMap.get(viewName);
   }
   
   
}
