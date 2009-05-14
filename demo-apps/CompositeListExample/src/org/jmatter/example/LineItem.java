package org.jmatter.example;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.IntEO;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class LineItem extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"qty", "item"};

   public LineItem()
   {
      qty.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e)
         {
            updateLineItemAndCart();
         }
      });
      association("item").addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt)
         {
            updateLineItemAndCart();
         }
      });
   }

   private void updateLineItemAndCart()
   {
      fireStateChanged();  // updates the title
      Cart cart = ((Cart) parentObject());
      if (cart == null) return;
      cart.updateTotal();
   }


   @Override
   public void initialize()
   {
      super.initialize();
      qty.setValue(1);
   }

   private final IntEO qty = new IntEO();
   public IntEO getQty() { return qty; }

   private Product item;
   public Product getItem() { return item; }
   public void setItem(Product item)
   {
      Product oldItem = this.item;
      this.item = item;
      firePropertyChange("item", oldItem, this.item);
   }

   public double amount()
   {
      if (item == null) return 0;
      return qty.intValue() * item.getPrice().doubleValue();
   }

   public Title title()
   {
      return qty.title().append("x ", item);
   }
}
