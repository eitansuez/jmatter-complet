/*
 * Created on Apr 25, 2005
 */
package com.u2d.find;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.app.Context;
import com.u2d.element.CommandInfo;
import com.u2d.list.CompositeList;
import com.u2d.list.CriteriaListEO;
import com.u2d.list.PagedList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.Title;
import com.u2d.model.AbstractListEO;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.view.EView;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Arg;

/**
 * @author Eitan Suez
 */
public class CompositeQuery extends AbstractComplexEObject
                            implements Query
{
   private final StringEO _name = new StringEO();
   private ComplexType _queryType;
   private final CompositeList _querySpecifications
                        = new CompositeList(QuerySpecification.class);
   public static Class querySpecificationsType = QuerySpecification.class;

   public static String[] fieldOrder = {"name", "queryType", "querySpecifications"};
   public static String pluralName() { return "Queries"; }
   
   public static String defaultSearchPath = "name";

   
   public CompositeQuery() {}

   public CompositeQuery(ComplexType queryType)
   {
      _queryType = queryType;
   }

   public StringEO getName() { return _name; }

   public ComplexType getQueryType() { return _queryType; }
   public void setQueryType(ComplexType type)
   {
      ComplexType oldType = _queryType;
      _queryType = type;
      firePropertyChange("queryType", oldType, _queryType);
   }

   public CompositeList getQuerySpecifications() { return _querySpecifications; }


   public int validate()
   {
      int errCount = super.validate();
      if (_querySpecifications.isEmpty())
      {
         _querySpecifications.fireValidationException("No query criteria specified.");
         errCount++;
      }
      return errCount;
   }

   public Criteria getCriteria()
   {
      HBMPersistenceMechanism p = hbmPersistor();

      try
      {
         Session s  = p.getSession();
         Criteria c = s.createCriteria(_queryType.getJavaClass());
         addExpression(c);
         return c;
      }
      catch (HibernateException ex)
      {
         System.err.println("Hibernate Exception: "+ex.getMessage());
         ex.printStackTrace();
         throw new RuntimeException(ex);
      }
   }

   /**
    * overload command new..
    */
   @Cmd
   public static CompositeQuery New(CommandInfo cmdInfo,
                                    @Arg("Query Type") ComplexType querytype)
   {
      ComplexType type = ComplexType.forClass(CompositeQuery.class);
      CompositeQuery query = (CompositeQuery) type.New(cmdInfo);
      query.setQueryType(querytype);
      return query;
   }

   @Cmd(mnemonic='x')
   public CriteriaListEO Execute(CommandInfo cmdInfo)
   {
      return new PagedList(this);
   }
   public CriteriaListEO execute() { return Execute(null); }

   private void addExpression(Criteria c) throws HibernateException
   {
      QuerySpecification spec = null;
      for (int i=0; i<_querySpecifications.getSize(); i++)
      {
         spec = (QuerySpecification) _querySpecifications.getElementAt(i);
         spec.addExpression(c);
      }
   }

   public Title title() { return _name.title(); }


   public EView getMainView()
   {
      if (_queryType == null)
         return getFormView();  // not yet working..
      // (compositequery was designed to work with queryview)

      return vmech().getQueryView(this);
   }

   public void onCreate()
   {
      super.onCreate();
      _queryType.addQuery(this);
   }
   public void onDelete()
   {
      super.onDelete();
      _queryType.removeQuery(this);
   }

   @Cmd
   public static AbstractListEO HQLQuery(CommandInfo cmdInfo,
                                         @Arg("HQL Text") TextEO hqlText)
   {
      String hql = hqlText.stringValue();
      HBMPersistenceMechanism hbm = Context.getInstance().hbmpersitor();
      return hbm.hql(hql);
   }

}
