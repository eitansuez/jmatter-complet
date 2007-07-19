/*
 * Created on Mar 30, 2005
 */
package com.u2d.type;

import java.util.Iterator;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.AbstractListEO;
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
   
   private AbstractListEO _list;
   
   public AbstractListEO list()
   {
      if (_list == null)
      {
         _list = type().list();
      }
      return _list;
   }

   public AbstractChoiceEO first() { return (AbstractChoiceEO) list().first(); }

   public AbstractChoiceEO get(String code)
   {
      AbstractChoiceEO eo;
      for (Iterator itr = list().iterator(); itr.hasNext(); )
      {
         eo = (AbstractChoiceEO) itr.next();
         if (eo.code().equals(code))
            return eo;
      }
      return null;
   }
   
   // ----

   public Object getSelectedItem()
   {
      return selectedItem;
   }
   
   Object selectedItem = this;
   
   public void setSelectedItem(Object anItem)
   {
      selectedItem = anItem;
      AbstractChoiceEO choice = (AbstractChoiceEO) anItem;
      setValue(choice);
   }
   public Object getElementAt(int index) { return list().getElementAt(index); }
   public int getSize() { return list().getSize(); }

   public void addListDataListener(ListDataListener l)
   {
      list().addListDataListener(l);
   }

   public void removeListDataListener(ListDataListener l)
   {
      list().removeListDataListener(l);
   }
}
