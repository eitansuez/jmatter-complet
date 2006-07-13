/*
 * Created on Mar 24, 2005
 */
package com.u2d.type.composite;

import com.u2d.app.PersistenceMechanism;
import com.u2d.app.AppFactory;
import com.u2d.list.RelationalList;
import com.u2d.model.*;
import com.u2d.type.atom.StringEO;
import com.u2d.view.EView;
import com.u2d.element.CommandInfo;
import com.u2d.persist.HBMSingleSession;
import com.u2d.reflection.CommandAt;
import com.u2d.reflection.ParamAt;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import org.hibernate.Session;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Eitan Suez
 */
public class Folder extends AbstractComplexEObject
{
   protected final StringEO _name = new StringEO();

   protected final RelationalList _items = new RelationalList(ComplexEObject.class);
   public static Class itemsType = ComplexEObject.class;
   public static int itemsRelationType = PersistenceMechanism.MANY_TO_MANY;


   public static String[] fieldOrder = {"name", "items"};

   public Folder() {}


   @CommandAt
   public static Folder NewWithName(CommandInfo cmdInfo, 
                                    @ParamAt("Folder name:") StringEO name)
   {
      ComplexType folderType = ComplexType.forClass(Folder.class);
      Folder folder = (Folder) folderType.New(cmdInfo);
      folder.getName().setValue(name);
      folder.save();
      return folder;
   }
   
   public StringEO getName() { return _name; }
   public RelationalList getItems() { return _items; }

   public EView getMainView()
   {
      return vmech().getAlternateView(this,
                                      new String[] {"folderview", "formview",
                                            "treeview", "omniview", "outlookview"});
   }

   // conveniences:
   public void addItem(ComplexEObject item) { _items.add(item); }
   public Object get(int index) { return getItems().getElementAt(index); }
   public int size() { return getItems().getTotal(); }

   public Title title() { return _name.title(); }


   private TreeModel _treeModel;
   public TreeModel treeModel()
   {
      if (_treeModel == null) _treeModel = new Folder.ETreeModel();
      return _treeModel;
   }

   class ETreeModel implements TreeModel
   {
      public Object getRoot() { return Folder.this; }
      public Object getChild(Object parent, int index)
      {
         if (parent instanceof Folder)
         {
            return ((Folder) parent).get(index);
         }

         throw new RuntimeException("Folder.TreeModel.getChild:  " +
               "Only folders should be non-leaf nodes; " +
               "Parent: " + parent + " index " + index);
      }
      public int getChildCount(Object parent)
      {
         if (parent instanceof Folder)
         {
            return ((Folder) parent).size();
         }
         throw new RuntimeException("Folder.TreeModel.getChildCound:  " +
               "Only folders should be non-leaf nodes; " +
               "Parent: " + parent);
      }
      public boolean isLeaf(Object node)
      {
         return (!(node instanceof Folder));
      }

      public int getIndexOfChild(Object parent, Object child)
      {
         if (parent instanceof Folder)
         {
            Folder folder = (Folder) parent;
            for (int i=0; i<folder.size(); i++)
            {
               if (child.equals(folder.get(i)))
                  return i;
            }
         }
         return -1;
      }

      public void addTreeModelListener(TreeModelListener listener) {}
      public void removeTreeModelListener(TreeModelListener listener) {}
      public void valueForPathChanged(TreePath path, Object newValue)
      {
         ComplexEObject ceo = null;
         for (int i=0; i<path.getPathCount(); i++)
         {
            ceo = (ComplexEObject) path.getPathComponent(i);
            ceo.fireStateChanged();
         }
      }
   }

   public static Folder fetchFolderByName(String name)
   {
      PersistenceMechanism pmech = AppFactory.getInstance().getApp().getPersistenceMechanism();
      if (pmech instanceof HBMSingleSession)
      {
         HBMSingleSession hbp = (HBMSingleSession) pmech; 
         Session s = hbp.getSession();
         Folder folder = (Folder) s.createQuery("from Folder where name = :name")
               .setParameter("name", name)
               .uniqueResult();
         if (folder != null)
            folder.setReadState();
         return folder;
      }
      else
      {
         return new Folder();
      }
   }


   // convenience..
   public void save()
   {
      Set folders = getNestedFolderSet(this, new HashSet());
      ((HBMSingleSession) persistor()).saveMany(folders);
   }

   private Set getNestedFolderSet(Folder folder, Set folders)
   {
      folders.add(folder);

      ComplexEObject item = null;
      for (int i=0; i<folder.size(); i++)
      {
         item = (ComplexEObject) folder.get(i);
         if (item instanceof Folder)
         {
            getNestedFolderSet((Folder) item, folders);
         }
      }
      return folders;
   }



}
