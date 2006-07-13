package com.u2d.persist.type;

import com.u2d.type.atom.CharEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Date: May 24, 2005
 * Time: 7:01:14 PM
 *
 * @author Eitan Suez
 */
public class CharEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof CharEO))) return false;
      return ((CharEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String checkValue = rs.getString(names[0]);
      if (checkValue.length() == 0)  // this condition turns out to be true with MySQL..
      {
         return new CharEO();
      }
      else
      {
         Character value = (Character) Hibernate.CHARACTER.nullSafeGet(rs, names[0]);
         return (value==null) ? null : new CharEO(value.charValue());
      }
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      char charvalue = (value==null) ? ' ' : ((CharEO) value).charValue();
      if (charvalue == '\0') charvalue = ' ';
      Hibernate.CHARACTER.nullSafeSet(pstmt, new Character(charvalue), index);
   }

   public Class returnedClass() { return CharEO.class; }

   private static final int[] TYPES = { java.sql.Types.CHAR };
   public int[] sqlTypes() { return TYPES; }

}
