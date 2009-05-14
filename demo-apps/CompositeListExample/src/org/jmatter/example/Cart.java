package org.jmatter.example;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexEObject;
import com.u2d.type.atom.*;
import com.u2d.list.CompositeList;
import com.u2d.reflection.Fld;
import javax.persistence.Entity;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

@Entity
public class Cart extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"date", "items", "total"};
   public static String[] readOnly = {"date", "total"};

   public Cart()
   {
      items.addListDataListener(new ListDataListener() {
         public void intervalAdded(ListDataEvent e) { updateTotal(); }
         public void intervalRemoved(ListDataEvent e) { updateTotal(); }
         public void contentsChanged(ListDataEvent e) { updateTotal(); }
      });
   }

   @Override
   public void onLoad()
   {
      super.onLoad();
      updateTotal();
   }

   public void updateTotal()
   {
      double sum = 0;
      for (int i=0; i<items.getSize(); i++)
      {
         LineItem item = (LineItem) items.get(i);
         sum += item.amount();
      }
      total.setValue(sum);
   }

   private final DateTime date = new DateTime();
   public DateTime getDate() { return date; }

   private Customer customer;
   public Customer getCustomer() { return customer; }
   public void setCustomer(Customer customer)
   {
      Customer oldCustomer = this.customer;
      this.customer = customer;
      firePropertyChange("customer", oldCustomer, this.customer);
   }

   private final USDollar total = new USDollar();
   @Fld(persist=false)
   public USDollar getTotal() { return total; }

   private final CompositeList items = new CompositeList(LineItem.class, this)
   {
      @Override
      public void add(int index, ComplexEObject item)
      {
         LineItem lineitem = (LineItem) item;
         if (!reduceCart(lineitem))
         {
            super.add(index, item);
         }
      }
   };
   public static Class itemsType = LineItem.class;
   public CompositeList getItems() { return items; }

   private boolean reduceCart(LineItem newItem)
   {
      Product p = newItem.getItem();
      if (p == null) return false;
      for (int i=0; i<items.getSize(); i++)
      {
         LineItem li = (LineItem) items.get(i);
         if (p.equals(li.getItem()))
         {
            li.getQty().increment();
            li.fireStateChanged();
            return true;
         }
      }
      return false;
   }

   public Title title()
   {
      if (customer == null) return new Title("--");
      return new Title(String.format("%tD %s %s", date.dateValue(), customer.getName(), total));
   }
}
