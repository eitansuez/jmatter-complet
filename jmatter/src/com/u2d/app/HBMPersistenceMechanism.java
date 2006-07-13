/*
 * Created on May 6, 2004
 */
package com.u2d.app;

import org.hibernate.Session;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;

/**
 * @author Eitan Suez
 */
public interface HBMPersistenceMechanism extends PersistenceMechanism
{
   public Session getSession();
   public ComplexEObject fetch(String query);
   public AbstractListEO hql(String query);
}
