/*
 * Created on Mar 3, 2005
 */
package com.u2d.find;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import com.u2d.element.Field;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Title;

/**
 * @author Eitan Suez
 */
public class QuerySpecification
      extends AbstractComplexEObject
{
   private final FieldPath _fieldPath = new FieldPath();
   private Inequality _inequality;
   private EObject _value;
   
   public static String[] fieldOrder = {"fieldPath", "inequality", "value"};
   
   public QuerySpecification() {}
   
   public QuerySpecification(FieldPath path, Inequality inequality, EObject value)
   {
      _fieldPath.setValue(path);
      _inequality = inequality;
      _value = value;
   }
   
   public FieldPath getFieldPath() { return _fieldPath; }
   
   public Inequality getInequality() { return _inequality; }
   public void setInequality(Inequality ineq)
   {
      Inequality oldIneq = _inequality;
      _inequality = ineq;
      firePropertyChange("inequality", oldIneq, _inequality);
   }
   
   public EObject getValue() { return _value; }
   public void setValue(EObject value)
   {
      EObject oldValue = _value;
      _value = value;
      firePropertyChange("value", oldValue, _value);
   }
   
   public void addExpression(Criteria c) throws HibernateException
   {
      Criteria specified = _fieldPath.specify(c);
      Field field = _fieldPath.getLastField();
      _inequality.addExpression(specified, field, _value);
   }
   
   public Title title()
   {
      return new Title("query specification (tbd)");
   }
}
