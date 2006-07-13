package com.u2d.persist.type;

import java.io.Serializable;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.cache.CacheConcurrencyStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.mapping.PersistentClass;
import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 14, 2005
 * Time: 10:06:40 AM
 */
public class ComplexTypePersister extends EntityPersisterAdapter
{
   protected SessionFactoryImplementor factory;

   public ComplexTypePersister(
         PersistentClass model,
         CacheConcurrencyStrategy cache,
         SessionFactoryImplementor factory,
         Mapping mapping) {
      this.factory = factory;
   }

   public SessionFactoryImplementor getFactory() { return factory; }

   public Class getMappedClass(EntityMode entityMode)
   {
      checkEntityMode(entityMode);
      return getMappedClass();
   }

   public Class getMappedClass() { return ComplexType.class; }

   public Object[] getPropertyValues(Object object, EntityMode entityMode)
         throws HibernateException
   {
      checkEntityMode(entityMode);
      ComplexType type = (ComplexType) object;
      return new Object[]{entityClassName(type)};
   }

   public Object getPropertyValue(Object object, int i, EntityMode entityMode)
         throws HibernateException
   {
      checkEntityMode(entityMode);
      return entityClassName((ComplexType) object);
   }

   public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
         throws HibernateException
   {
      checkEntityMode(entityMode);
      return entityClassName((ComplexType) object);
   }

   public Serializable getIdentifier(Object object, EntityMode entityMode)
         throws HibernateException
   {
      checkEntityMode(entityMode);
      return entityClassName((ComplexType) object);
   }

   private String entityClassName(ComplexType type)
   {
      String clsName = type.getJavaClass().getName();
      // if cglib-enhanced class, strip to get original class name:
      int idx = clsName.indexOf("$$EnhancerByCGLIB$$");
      if (idx > 0) clsName = clsName.substring(0, idx);
      return clsName;
   }

   public Object instantiate(Serializable id, EntityMode entityMode)
         throws HibernateException
   {
      checkEntityMode(entityMode);
      return getComplexType((String) id);
   }

   private ComplexType getComplexType(String clsName)
         throws HibernateException
   {
      try
      {
         return ComplexType.forClass(Class.forName(clsName));
      }
      catch (ClassNotFoundException ex)
      {
         throw new HibernateException(ex);
      }
   }

   public boolean isInstance(Object object, EntityMode entityMode) {
      checkEntityMode( entityMode );
      return object instanceof ComplexType;
   }

   public Object load(
      Serializable id,
      Object optionalObject,
      LockMode lockMode,
      SessionImplementor session
   ) throws HibernateException {

      // fails when optional object is supplied

//      Custom clone = null;
//      Custom obj = (Custom) INSTANCES.get(id);
//      if (obj!=null) {
//         clone = (Custom) obj.clone();
//         TwoPhaseLoad.addUninitializedEntity(
//               new EntityKey( id, this, session.getEntityMode() ),
//               clone,
//               this,
//               LockMode.NONE,
//               false,
//               session
//            );
//         TwoPhaseLoad.postHydrate(
//               this, id,
//               new String[] { obj.getName() },
//               null,
//               clone,
//               LockMode.NONE,
//               false,
//               session
//            );
//         TwoPhaseLoad.initializeEntity(
//               clone,
//               false,
//               session,
//               new PreLoadEvent( (EventSource) session ),
//               new PostLoadEvent( (EventSource) session )
//            );
//      }
//      return clone;

      return getComplexType((String) id);
   }


   public void insert(
      Serializable id,
      Object[] fields,
      Object object,
      SessionImplementor session
   ) throws HibernateException {

      getComplexType((String) id);
   }

   protected static final String[] NAMES = new String[] { "clsname" };

   public String[] getPropertyNames() { return NAMES; }
   public String getIdentifierPropertyName() { return NAMES[0]; }
   public String getRootEntityName() { return "COMPLEXTYPES"; }
   public Serializable[] getPropertySpaces() { return new String[] { "COMPLEXTYPES" }; }
   public Serializable[] getQuerySpaces() { return new String[] { "COMPLEXTYPES" }; }

   public Object getCurrentVersion(
      Serializable id,
      SessionImplementor session)
      throws HibernateException {

      return getComplexType((String) id);
   }

}
