/*
 * Created on Jan 22, 2004
 */
package com.u2d.domain;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import javax.persistence.Entity;

/**
 * @author Eitan Suez
 */
@Entity
public class OrderItem extends AbstractComplexEObject
{
   public OrderItem() {}
   
   public OrderItem(String description)
   {
      _description.setValue(description);
   }
   
   private final StringEO _description = new StringEO();
   public StringEO getDescription() { return _description; }
   
   private final BigDecimalEO amount = new BigDecimalEO();
   public BigDecimalEO getAmount() { return amount; }

   public Title title() { return _description.title(); }
}
