/*
 * Created on Jan 31, 2004
 */
package com.u2d.app;

import com.u2d.list.PlainListEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;

/**
 * @author Eitan Suez
 */
public interface PersistenceMechanism
{
   public static final int ONE_TO_MANY = 1;
   public static final int MANY_TO_MANY = 2;
   
   public ComplexEObject load(Class clazz, Long id);
   public void save(ComplexEObject ceo);
   public void delete(ComplexEObject ceo);
   public PlainListEObject list(Class clazz);
   public PlainListEObject list(ComplexType type);
   public AbstractListEO browse(ComplexType type);  // browse pages
   
   public com.u2d.type.Choice lookup(Class clazz, String code);
   public ComplexEObject fetchSingle(Class clazz);
   public void updateAssociation(ComplexEObject one, ComplexEObject two);

   public boolean authenticate(String username, String password);
}
