/*
 * Created on May 6, 2004
 */
package com.u2d.app;

import org.hibernate.Session;
import org.hibernate.Query;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import java.util.Set;

/**
 * @author Eitan Suez
 */
public interface HBMPersistenceMechanism extends PersistenceMechanism
{
   public Session getSession();
   public ComplexEObject fetch(String query);
   public AbstractListEO hql(String query);

   public Set<Class> getClasses();
   public AbstractListEO hqlQuery(Query query);
   public void saveMany(Set ceos);
   public void deleteMany(Set ceos);
   public void refresh(ComplexEObject eo);
}
