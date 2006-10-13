/*
 * Created on May 6, 2004
 */
package com.u2d.app;

import org.hibernate.Session;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;

import java.util.Set;

/**
 * @author Eitan Suez
 */
public interface HBMPersistenceMechanism extends PersistenceMechanism
{
   public Set<Class> getClasses();
   public Session getSession();
   public ComplexEObject fetch(String query);
   public AbstractListEO hql(String query);
   public void saveMany(java.util.Set ceos);
}
