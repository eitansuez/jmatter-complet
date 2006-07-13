/*
 * Created on Mar 30, 2005
 */
package com.u2d.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.view.EView;

/**
 * @author Eitan Suez
 */
public abstract class AbstractChoiceEO extends AbstractComplexEObject
               implements Choice, ComboBoxModel
{
   public abstract StringEO getCode();
   public abstract StringEO getCaption();
   public abstract ComplexType choiceType();
   
   public String code() { return getCode().stringValue(); }
   public String caption() { return getCaption().stringValue(); }
   
   public Title title() { return getCaption().title(); }
   public String toString() { return title().toString(); }

   public EView getView() { return vmech().getChoiceView(this); }

   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (! (obj instanceof AbstractChoiceEO)) return false;

      AbstractChoiceEO choice = (AbstractChoiceEO) obj;
      return getClass().equals(choice.getClass()) &&
             code().equals(choice.code());
   }

   public int hashCode()
   {
      return 31 * getClass().hashCode() + code().hashCode();
   }


   private static Map _map = new HashMap();
   
   public AbstractListEO list()
   {
      ComplexType type = choiceType();
      if (_map.get(type.getJavaClass()) == null)
      {
         AbstractListEO leo = type.list();
         _map.put(type.getJavaClass(), leo);
      }
      return (AbstractListEO) _map.get(type.getJavaClass());
   }
   
   public AbstractChoiceEO first()
   {
      if (list().getSize() > 0)
         return (AbstractChoiceEO) list().getItems().get(0);
      return null;
   }
   
   public AbstractChoiceEO get(String code)
   {
      AbstractListEO leo = list();
      Iterator itr = leo.iterator();
      AbstractChoiceEO eo;
      while (itr.hasNext())
      {
         eo = (AbstractChoiceEO) itr.next();
         if (eo.code().equals(code))
            return eo;
      }
      return null;
   }
   
   // ----
   
   public Object getSelectedItem() { return this; }
   public void setSelectedItem(Object anItem)
   {
      AbstractChoiceEO choice = (AbstractChoiceEO) anItem;
      setValue(choice);
   }
   public Object getElementAt(int index) { return list().getElementAt(index); }
   public int getSize() { return list().getSize(); }
   public void addListDataListener(ListDataListener l) { list().addListDataListener(l); }
   public void removeListDataListener(ListDataListener l) { list().removeListDataListener(l); }
}
