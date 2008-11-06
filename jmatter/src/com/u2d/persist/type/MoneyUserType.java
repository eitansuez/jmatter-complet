package com.u2d.persist.type;

import org.hibernate.usertype.CompositeUserType;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import com.u2d.type.atom.Money;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Date: May 24, 2005
 * Time: 7:20:17 PM
 *
 * @author Eitan Suez
 */
public class MoneyUserType implements CompositeUserType
{
   public Object deepCopy(Object obj)
   {
      if (obj == null || (!(obj instanceof Money))) return null;
      return ((Money) obj).makeCopy();
   }

   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof Money))) return false;
      return x.equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names,
         SessionImplementor session, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      BigDecimal amount = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet(rs, names[0]);
      String currencyCode = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
      return new Money(amount, currencyCode);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value,
         int index, SessionImplementor session)
      throws HibernateException, java.sql.SQLException
   {
      Money m = (Money) value;
      Hibernate.BIG_DECIMAL.nullSafeSet(pstmt, m.amount(), index);
      Hibernate.STRING.nullSafeSet(pstmt, m.currency().getCurrencyCode(), index + 1);
   }

   public boolean isMutable() { return true; }
   public Class returnedClass() { return Money.class; }

   public static final int[] TYPES = { java.sql.Types.DECIMAL, java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public static String[] COLUMNNAMES = {"amount", "currency"};


   public String[] getPropertyNames()
   {
      return new String[] {"amount", "currency"};
   }

   public Type[] getPropertyTypes()
   {
      return new Type[] { Hibernate.BIG_DECIMAL, Hibernate.STRING };
   }

   public Object getPropertyValue(Object component, int property)
   {
      Money m = (Money) component;
      if (property == 0)
         return m.amount();
      else
         return m.currency().getCurrencyCode();
   }

   public void setPropertyValue(Object component, int property, Object value)
   {
      Money money = (Money) component;
      if (property == 0)
      {
         money.amount((BigDecimal) value);
      }
      else
      {
         money.currency(Currency.getInstance((String) value));
      }
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
      return x.hashCode();
   }

   public Object replace(Object original, Object target, SessionImplementor session, Object owner)
         throws HibernateException
   {
      return deepCopy(original);
   }
}