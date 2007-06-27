/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

import com.u2d.view.*;
import com.u2d.field.IndexedField;
import com.u2d.field.Association;
import com.u2d.field.DynaAssociationStrategy;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.element.Field;
import com.u2d.app.Context;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author Eitan Suez
 */
public class ContainsInequality
{
   private EView _ineqView;
   private List _inequalities;

   public ContainsInequality()
   {
      _inequalities = new ArrayList();
      _inequalities.add(new Contains());
   }

   public ContainsInequality(IndexedField field)
   {
      this();
      DynaAssociationStrategy das = new DynaAssociationStrategy(field.fieldtype());
      Association typeAssociation = new Association(das);
      _ineqView = Context.getInstance().getViewMechanism().getAssociationView(typeAssociation);
   }
   
   public List getInequalities() { return _inequalities; }

   
   public class Contains extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criteria subCriteria = criteria.createCriteria(field.getCleanPath());
         subCriteria.add(Restrictions.eq("id", ((ComplexEObject) eo).getID()));
      }

      public EView getValueEditor() { return _ineqView; }

      public EObject getValue()
      {
         return _ineqView.getEObject();
      }

      public String toString() { return "contains"; }
   }

}
