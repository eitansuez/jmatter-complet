/*
 * Created on Nov 3, 2003
 */
package com.u2d.find;

import com.u2d.element.Field;
import com.u2d.model.EObject;
import com.u2d.view.EView;
import org.hibernate.Criteria;

/**
 * @author Eitan Suez
 */
public interface Inequality extends EObject
{
   public void addExpression(Criteria criteria, Field field, EObject value);
	public EView getValueEditor();
   public EObject getValue();
}
