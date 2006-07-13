/*
 * Created on Mar 15, 2004
 */
package com.u2d.persist;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.property.*;
import com.u2d.element.Field;
import com.u2d.model.*;

/**
 * @author Eitan Suez
 */
public class PropertyAccessorAdapter implements PropertyAccessor
{
   public Getter getGetter(Class clazz, String propertyName)
         throws PropertyNotFoundException
   {
      ComplexType type = ComplexType.forClass(clazz);
      Field field = type.field(propertyName);
      if (field == null)
         throw new PropertyNotFoundException("No such field: " + propertyName +
               " on class "+clazz.getName());
      return new GetterAdapter(field);
   }

   public Setter getSetter(Class clazz, String propertyName)
         throws PropertyNotFoundException
   {
      ComplexType type = ComplexType.forClass(clazz);
      Field field = type.field(propertyName);
      if (field == null)
         throw new PropertyNotFoundException("No such field: " + propertyName +
               " on class "+clazz.getName());
      return new SetterAdapter(field);
   }

   class GetterAdapter implements Getter
   {
      Field _field;

      GetterAdapter(Field field) { _field = field; }

      public Object get(Object target) throws HibernateException
      {
         Object value = _field.get((ComplexEObject) target);

         // technically i should have a NullField type so that
         // when i do a .get() on it it will automatically return
         // null and i won't need these if's!
         if (value instanceof NullComplexEObject)
            return null;

         if (value instanceof AbstractListEO)
         {
            return ((AbstractListEO) value).getItems();
         }

         return value;
      }
      public Method getMethod() { return null; }
      public String getMethodName() { return null; }
      public Class getReturnType()
      {
         if (_field.isIndexed())
            return Collection.class;
         return _field.getJavaClass();
      }

      public Object getForInsert(Object owner,
                                 Map mergeMap,
                                 SessionImplementor session) throws HibernateException
      {
         return get(owner);
      }
   }

   class SetterAdapter implements Setter
   {
      Field _field;

      SetterAdapter(Field field) { _field = field; }

      public Method getMethod() { return null; }
      public String getMethodName() { return null; }

      public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
      {
         _field.restore((ComplexEObject) target, value);
      }
   }

}
