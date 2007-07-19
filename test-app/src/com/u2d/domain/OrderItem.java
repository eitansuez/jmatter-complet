/*
 * Created on Jan 22, 2004
 */
package com.u2d.domain;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.persist.Persist;

/**
 * @author Eitan Suez
 */
@Persist
public class OrderItem extends AbstractComplexEObject
{
   private final StringEO _description = new StringEO();
   
   public OrderItem() {}
   
   public OrderItem(String description)
   {
      _description.setValue(description);
   }
   
   public StringEO getDescription() { return _description; }
   
   public Title title() { return _description.title(); }
}
