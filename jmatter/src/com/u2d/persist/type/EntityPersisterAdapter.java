package com.u2d.persist.type;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.*;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.Assigned;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.sql.QuerySelect;
import org.hibernate.sql.Select;
import org.hibernate.type.VersionType;
import org.hibernate.type.Type;
import org.hibernate.util.EqualsHelper;
import org.hibernate.cache.entry.CacheEntryStructure;
import org.hibernate.cache.entry.UnstructuredCacheEntry;
import org.hibernate.cache.CacheConcurrencyStrategy;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.CascadeStyle;
import java.io.Serializable;
import java.util.Map;

/**
 * The goal is to [again] study my single/working implementation
 * of ComplexTypePersister and put the junk in this class
 * so that i don't have to start from scratch each time.
 */
public abstract class EntityPersisterAdapter implements EntityPersister
{

   /**
    * @see EntityPersister#lock(java.io.Serializable, Object, Object, org.hibernate.LockMode, org.hibernate.engine.SessionImplementor)
    */
   public void lock(
      Serializable id,
      Object version,
      Object object,
      LockMode lockMode,
      SessionImplementor session
   ) throws HibernateException {

      throw new UnsupportedOperationException();
   }

   public boolean hasInsertGeneratedProperties() { return false; }
   public boolean hasUpdateGeneratedProperties() { return false; }
   public void processInsertGeneratedProperties(Serializable id,
                                                Object entity,
                                                Object[] state,
                                                SessionImplementor session)
   {}
   public void processUpdateGeneratedProperties(Serializable id,
                                                Object entity,
                                                Object[] state,
                                                SessionImplementor session)
   {}

   public CacheEntryStructure getCacheEntryStructure() { return new UnstructuredCacheEntry(); }
   public boolean hasSubselectLoadableCollections() { return false; }
   public boolean hasMutableProperties() { return false; }
   public boolean isInstrumented(EntityMode entityMode) { return false; }
   public boolean[] getPropertyLaziness() { return null; }
   public boolean isLazyPropertiesCacheable() { return true; }
   public boolean hasGeneratedProperties() { return false; }
   public boolean isVersionPropertyGenerated() { return false; }

   public boolean hasLazyProperties() { return false; }

   protected void checkEntityMode(EntityMode entityMode) {
      if ( EntityMode.POJO != entityMode ) {
         throw new IllegalArgumentException( "Unhandled EntityMode : " + entityMode );
      }
   }

   public boolean isInherited() { return false; }

   public boolean implementsLifecycle(EntityMode entityMode) {
      checkEntityMode( entityMode );
      return false;
   }

   public boolean implementsValidatable(EntityMode entityMode) {
      checkEntityMode( entityMode );
      return false;
   }

   public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
      checkEntityMode( entityMode );
      return null;
   }

   public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
      checkEntityMode( entityMode );
      return false;
   }

   public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
      checkEntityMode( entityMode );
      return this;
   }

   public int[] findModified(
      Object[] x,
      Object[] y,
      Object owner,
      SessionImplementor session
   ) throws HibernateException {
      if ( !EqualsHelper.equals( x[0], y[0] ) ) {
         return new int[] { 0 };
      }
      else {
         return null;
      }
   }

   public boolean hasIdentifierProperty() {
      return true;
   }

   public boolean isVersioned() { return false; }

   public VersionType getVersionType() { return null; }

   public int getVersionProperty() { return 0; }

   public Serializable insert(Object[] fields, Object object, SessionImplementor session)
   throws HibernateException {

      throw new UnsupportedOperationException();
   }

   public void delete(
      Serializable id,
      Object version,
      Object object,
      SessionImplementor session
   ) throws HibernateException {

//      INSTANCES.remove(id);
   }

   /**
    * @see EntityPersister
    */
   public void update(
      Serializable id,
      Object[] fields,
      int[] dirtyFields,
      boolean hasDirtyCollection,
      Object[] oldFields,
      Object oldVersion,
      Object object,
      Object rowId,
      SessionImplementor session
   ) throws HibernateException {

//      INSTANCES.put( id, ( (Custom) object ).clone() );

   }

   protected static final Type[] TYPES = new Type[] { Hibernate.STRING };
   protected static final boolean[] MUTABILITY = new boolean[] { false };
   protected static final boolean[] GENERATION = new boolean[] { false };
   protected static final boolean[] INSERTABILITY = new boolean[] { true };
   protected static final int[] NATURALIDENTIFIERS = new int[] { 0 };

   public Type[] getPropertyTypes() { return TYPES; }
   public CascadeStyle[] getPropertyCascadeStyles() { return null; }

   public Type getIdentifierType() { return TYPES[0]; }

   public boolean hasNaturalIdentifier() { return true; }
   public Type[] getNaturalIdentifierTypes() { return TYPES; }
   public int[] getNaturalIdentifierProperties() {
      return NATURALIDENTIFIERS;
   }

   public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
         throws HibernateException
   {
      return null;
   }


   public boolean hasCache() { return false; }
   public CacheConcurrencyStrategy getCache() { return null; }

   public boolean isDynamic() { return false; }
   public boolean isCacheInvalidationRequired() { return false; }

   public void applyFilters(QuerySelect select, String alias, Map filters) { }
   public void applyFilters(Select select, String alias, Map filters) { }

   public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) { }
   public void afterReassociate(Object entity, SessionImplementor session) { }

   public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
   throws HibernateException {
      return null;
   }

   public boolean[] getPropertyVersionability() { return MUTABILITY; }

   // new methods added to interface as of 3.1rc1
   // just guessing that i don't need these..
   public boolean[] getPropertyInsertGeneration() { return GENERATION; }
   public boolean[] getPropertyUpdateGeneration() { return GENERATION; }


   public EntityMode guessEntityMode(Object object) {
      if ( !isInstance(object, EntityMode.POJO) ) {
         return null;
      }
      else {
         return EntityMode.POJO;
      }
   }

   public boolean[] getPropertyNullability() {
      return MUTABILITY;
   }

   public ClassMetadata getClassMetadata() { return null; }

   public boolean[] getPropertyUpdateability() { return MUTABILITY; }
   public boolean[] getPropertyCheckability() { return MUTABILITY; }

   public boolean[] getPropertyInsertability() { return INSERTABILITY; }

   /**
    * Which of the properties of this class are database generated values?
    */
   public boolean[] getPropertyGeneration() { return GENERATION; }

   public boolean hasIdentifierPropertyOrEmbeddedCompositeIdentifier() {
      return true;
   }

   public boolean isBatchLoadable() { return false; }

   public Type getPropertyType(String propertyName) {
      throw new UnsupportedOperationException();
   }

   public Object getPropertyValue(Object object, String propertyName)
      throws HibernateException {
      throw new UnsupportedOperationException();
   }

   public Object createProxy(Serializable id, SessionImplementor session)
      throws HibernateException {
      throw new UnsupportedOperationException("no proxy for this class");
   }

   public void postInstantiate() throws MappingException {}

   public boolean hasProxy() { return false; }

   public boolean hasCollections() { return false; }

   public boolean hasCascades() { return false; }

   public boolean isMutable() { return false; }

   public boolean isSelectBeforeUpdateRequired() { return false; }

   public boolean isIdentifierAssignedByInsert() {
      return false;
   }

   public Boolean isTransient(Object object, SessionImplementor session) {
      return Boolean.FALSE;
   }

   public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
      checkEntityMode( entityMode );
      setPropertyValue( object, 0, values[0], entityMode );
   }

   public int[] findDirty(
      Object[] x,
      Object[] y,
      Object owner,
      SessionImplementor session
   ) throws HibernateException {
      if ( !EqualsHelper.equals( x[0], y[0] ) ) {
         return new int[] { 0 };
      }
      else {
         return null;
      }
   }

   protected static final IdentifierGenerator GENERATOR =
         new Assigned();

   /**
    * @see EntityPersister#getIdentifierGenerator()
    */
   public IdentifierGenerator getIdentifierGenerator()
   throws HibernateException {
      return GENERATOR;
   }

   public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
   throws HibernateException {
      return getPropertyValues( object, session.getEntityMode() );
   }

   public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
      throw new UnsupportedOperationException();
   }

   

   public abstract Class getMappedClass();
   public String getEntityName() { return getMappedClass().getName(); }

   public boolean isSubclassEntityName(String entityName) {
      return getMappedClass().getName().equals(entityName);
   }

   public Class getConcreteProxyClass(EntityMode entityMode) {
      checkEntityMode( entityMode );
      return getMappedClass();
   }

   
   public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
      checkEntityMode( entityMode );
//      ( ( Custom ) entity ).id = ( String ) currentId;
   }

   public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode) throws HibernateException {
      checkEntityMode( entityMode );
//      ( (Custom) object ).setName( (String) value );
   }

   public void setIdentifier(Object object, Serializable id, EntityMode entityMode) throws HibernateException {
      checkEntityMode( entityMode );
//      ( (Custom) object ).id = (String) id;
   }


   public Object forceVersionIncrement(Serializable serializable, Object object, SessionImplementor sessionImplementor)
         throws HibernateException
   {
      return null;
   }
   
   
}
