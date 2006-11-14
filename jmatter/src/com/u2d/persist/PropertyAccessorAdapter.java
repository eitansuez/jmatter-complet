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
      return new GetterAdapter(clazz, propertyName);
   }

   public Setter getSetter(Class clazz, String propertyName)
         throws PropertyNotFoundException
   {
      return new SetterAdapter(clazz, propertyName);
   }
   
   abstract class PropertyAdapter
   {
      private Class _clazz;
      private String _propertyName;
      private Field _field;
      
      protected Field field()
      {
         if (_field == null)
         {
            ComplexType type = ComplexType.forClass(_clazz);
            Field field = type.field(_propertyName);
            if (field == null)
               throw new PropertyNotFoundException("No such field: " + _propertyName +
                     " on class "+_clazz.getName());
            _field = field;
         }
         return _field;
      }

      PropertyAdapter(Class clazz, String propertyName)
      {
         _clazz = clazz;  _propertyName = propertyName;
      }
   }

   class GetterAdapter extends PropertyAdapter implements Getter
   {
      GetterAdapter(Class clazz, String propertyName)
      {
         super(clazz, propertyName);
      }

      public Object get(Object target) throws HibernateException
      {
         Object value = field().get((ComplexEObject) target);

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
         if (field().isIndexed())
            return Collection.class;
         return field().getJavaClass();
      }

      public Object getForInsert(Object owner,
                                 Map mergeMap,
                                 SessionImplementor session) throws HibernateException
      {
         return get(owner);
      }
   }

   class SetterAdapter extends PropertyAdapter implements Setter
   {
      SetterAdapter(Class clazz, String propertyName)
      {
         super(clazz, propertyName);
      }

      public Method getMethod() { return null; }
      public String getMethodName() { return null; }

      public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
      {
         field().restore((ComplexEObject) target, value);
      }
   }
   
}
