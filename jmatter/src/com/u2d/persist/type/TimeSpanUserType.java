package com.u2d.persist.type;

import org.hibernate.usertype.CompositeUserType;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import com.u2d.type.atom.TimeSpan;
import java.util.Date;

/**
 * Date: May 24, 2005
 * Time: 7:20:17 PM
 *
 * @author Eitan Suez
 */
public class TimeSpanUserType implements CompositeUserType
{
   public Object deepCopy(Object obj)
   {
      if (obj == null || (!(obj instanceof TimeSpan))) return null;
      return ((TimeSpan) obj).makeCopy();
   }

   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof TimeSpan))) return false;
      return x.equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names,
         SessionImplementor session, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Date from = (Date) Hibernate.TIMESTAMP.nullSafeGet(rs, names[0]);
      Date to = (Date) Hibernate.TIMESTAMP.nullSafeGet(rs, names[1]);
      return new TimeSpan(from, to);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value,
         int index, SessionImplementor session)
      throws HibernateException, java.sql.SQLException
   {
      TimeSpan span = (TimeSpan) value;
      Hibernate.TIMESTAMP.nullSafeSet(pstmt, span.startDate(), index);
      Hibernate.TIMESTAMP.nullSafeSet(pstmt, span.endDate(), index + 1);
   }

   public boolean isMutable() { return true; }
   public Class returnedClass() { return TimeSpan.class; }

   public static final int[] TYPES = { java.sql.Types.TIMESTAMP, java.sql.Types.TIMESTAMP };
   public int[] sqlTypes() { return TYPES; }

   public static String[] COLUMNNAMES = {"startDate", "endDate"};


   public String[] getPropertyNames()
   {
      return new String[] {"start", "end"};
   }

   public Type[] getPropertyTypes()
   {
      return new Type[] { Hibernate.TIMESTAMP, Hibernate.TIMESTAMP };
   }

   public Object getPropertyValue(Object component, int property)
   {
      TimeSpan span = (TimeSpan) component;
      if (property == 0)
         return span.startDate();
      else
         return span.endDate();
   }

   public void setPropertyValue(Object component, int property, Object value)
   {
      TimeSpan span = (TimeSpan) component;
      Date dateValue = (Date) value;
      if (property == 0)
         span.startDate(dateValue);
      else
         span.endDate(dateValue);
   }

   public Object assemble(java.io.Serializable cached,
         SessionImplementor session, Object owner)
   {
      return deepCopy(cached);
   }

   public java.io.Serializable disassemble(Object value,
         SessionImplementor session)
   {
      return (java.io.Serializable) deepCopy(value);
   }

   public int hashCode(Object x) throws HibernateException
   {
      TimeSpan ts = (TimeSpan) x;
      return ts.startDate().hashCode() + 31 * (ts.endDate().hashCode());
   }

   public Object replace(Object original, Object target, SessionImplementor session, Object owner)
         throws HibernateException
   {
      return deepCopy(original);
   }
}
