package com.u2d.persist.type;

import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.dom4j.Element;
import com.u2d.type.atom.ChoiceEO;
import com.u2d.type.Choice;
import com.u2d.model.EObject;
import com.u2d.element.Field;
import java.io.Serializable;

/**
 * Date: May 24, 2005
 * Time: 7:02:39 PM
 *
 * @author Eitan Suez
 */
public class ChoiceEOUserType implements CompositeUserType
{
   public boolean equals(Object x, Object y) throws HibernateException
   {
      if (x == null || (!(x instanceof ChoiceEO))) return false;
      return ((ChoiceEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names,
         SessionImplementor session, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String classname = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      String code = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
      ChoiceEO eo = null;
      try
      {
         Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classname);
         eo = (ChoiceEO) clazz.newInstance();
         eo.setValue(code);
      }
      catch (IllegalAccessException ex)
      {
         System.err.println("IllegalAccessException: "+ex.getMessage());
         ex.printStackTrace();
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
      }
      catch (InstantiationException ex)
      {
         System.err.println("InstantiationException: "+ex.getMessage());
         ex.printStackTrace();
      }
      return eo;
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value,
         int index, SessionImplementor session)
      throws HibernateException, java.sql.SQLException
   {
      String classname = (value==null) ? "" : value.getClass().getName();

      String code = "";
      if (value != null)
      {
         if (value instanceof String)
            code = (String) value;
         else if (value instanceof ChoiceEO)
            code = ((ChoiceEO) value).code();
      }

      Hibernate.STRING.nullSafeSet(pstmt, classname, index);
      Hibernate.STRING.nullSafeSet(pstmt, code, index + 1);
   }

   public Class returnedClass() { return ChoiceEO.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public String[] getPropertyNames() { return new String[] {"type", "code"}; }
   public Type[] getPropertyTypes() { return new Type[] { Hibernate.STRING,  Hibernate.STRING }; }

   public Object getPropertyValue(Object component, int property)
   {
      ChoiceEO choice = (ChoiceEO) component;
      if (property == 1)
         return choice.code();
      return choice.getClass().getName();
   }

   public void setPropertyValue(Object component, int property, Object value)
   {
      ChoiceEO choice = (ChoiceEO) component;
      if (property == 1)
         choice.setValue((String) value);
   }

   public int hashCode(Object x) throws HibernateException
   {
      return ((Choice) x).code().hashCode();
   }

   public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException
   {
      return (Serializable) deepCopy(value);
   }

   public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException
   {
      return deepCopy(cached);
   }

   public Object replace(Object original, Object target, SessionImplementor session, Object owner)
         throws HibernateException
   {
      return deepCopy(original);
   }

   public Object deepCopy(Object obj)
   {
      if (obj == null || (!(obj instanceof EObject))) return null;
      return ((EObject) obj).makeCopy();
   }

   public boolean isMutable() { return true; }

   public static void fillPropertyElement(Element propElem, Field field, String prefix)
   {
      String colprefix = (prefix == null) ? "" : prefix + "_";
      colprefix += field.getName();

      Element typecol = propElem.addElement("column");
      typecol.addAttribute("name", colprefix + "_type");
      typecol.addAttribute("default", String.format("'%s'", field.getJavaClass().getName()));

      Element codecol = propElem.addElement("column");
      codecol.addAttribute("name", colprefix + "_code");
      codecol.addAttribute("default", "''");
   }

}
