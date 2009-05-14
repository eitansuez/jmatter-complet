/*
 * Created on May 6, 2004
 */
package com.u2d.app;

import org.hibernate.Session;
import org.hibernate.Query;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.persist.HBMBlock;
import java.util.Set;

/**
 * @author Eitan Suez
 */
public interface HBMPersistenceMechanism extends PersistenceMechanism
{
   Session getSession();
   ComplexEObject fetch(String query);
   AbstractListEO hql(String query);

   Set<Class> getClasses();
   AbstractListEO hqlQuery(Query query);
   void saveMany(Set ceos);
   void deleteMany(Set ceos);
   void refresh(ComplexEObject eo);
   
   ComplexEObject lookup(Class clazz, String uniqueFieldName, String value);
   
   void transaction(HBMBlock block);
   
   void close();
}
