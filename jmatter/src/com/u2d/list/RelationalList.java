/*
 * Created on Mar 9, 2004
 */
package com.u2d.list;

import com.u2d.model.ComplexEObject;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.field.Association;
import java.util.Collections;

/**
 * @author Eitan Suez
 */
public class RelationalList extends SimpleListEO
            // implements Searchable  // "Criteria queries do not support collections of values" (gavin)
                                      // see http://opensource.atlassian.com/projects/hibernate/browse/HHH-869
                                      // still outstanding as of mid-2007
{
   private RelationalList() {}  // for jibx

   public RelationalList(Class clazz) { super(clazz); }

   public EView getView() { return vmech().getExpandableListView(this); }
   public EView getMainView() { return getListView(); }
   public ListEView getListView() { return vmech().getEditableListView(this); }


   public Association association()
   {
      return parentObject().association(field().name());
   }
   public void dissociate(ComplexEObject eo)
   {
      association().dissociateItem(eo);
   }


//   public List getInequalities()
//   {
//      return new ContainsInequality((IndexedField) field()).getInequalities();
//   }


   public void jibxAdd(Object obj)
   {
      if (!(obj instanceof ComplexEObject))
         throw new IllegalArgumentException("Cannot add object that is not a ComplexEObject");
      super.add((ComplexEObject) obj);
   }
   public java.util.Iterator jibxIterator()
   {
      return super.iterator();
   }


   /*
    * call only if you know that item type implements Comparable
    */
   public void sort()
   {
      Collections.sort(_items);
   }

}
