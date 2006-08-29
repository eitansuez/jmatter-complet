/*
 * Created on Mar 3, 2005
 */
package com.u2d.find;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.app.AppFactory;
import com.u2d.element.CommandInfo;
import com.u2d.list.CriteriaListEO;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.reflection.CommandAt;

/**
 * @author Eitan Suez
 */
public class SimpleQuery extends AbstractComplexEObject
                         implements Query
{
   private final StringEO _name = new StringEO();
   private ComplexType _queryType;
   private QuerySpecification _querySpecification;
   
   public static String defaultSearchPath = "name";

   public SimpleQuery(ComplexType type)
   {
      _queryType = type;
   }
   public SimpleQuery(ComplexType type, QuerySpecification spec)
   {
      this(type);
      _querySpecification = spec;
   }

   public StringEO getName() { return _name; }

   public ComplexType getQueryType() { return _queryType; }
   public void setQueryType(ComplexType type)
   {
      ComplexType oldType = _queryType;
      _queryType = type;
      firePropertyChange("queryType", oldType, _queryType);
   }

   public QuerySpecification getQuerySpecification() { return _querySpecification; }

   @CommandAt(mnemonic='x')
   public CriteriaListEO Execute(CommandInfo cmdInfo)
   {
      return new CriteriaListEO(this);
   }
   public CriteriaListEO execute() { return Execute(null); }
   

   public Criteria getCriteria()
   {
      HBMPersistenceMechanism p =
         (HBMPersistenceMechanism) AppFactory.getInstance().getApp().getPersistenceMechanism();
      return getCriteria(p.getSession());
   }

   public Criteria getCriteria(Session s)
   {
      try
      {
         Criteria c = s.createCriteria(_queryType.getJavaClass());
         addExpression(c);
         return c;
      }
      catch (HibernateException ex)
      {
         System.err.println("HibernateException: "+ex.getMessage());
         ex.printStackTrace();
         throw new RuntimeException(ex);
      }
   }

   private void addExpression(Criteria c) throws HibernateException
   {
      if (_querySpecification != null)
         _querySpecification.addExpression(c);
   }

   public Title title() { return _name.title(); }
}
