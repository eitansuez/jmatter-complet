package com.u2d.persist.type;

import com.u2d.type.atom.Logo;
import com.u2d.type.atom.ImgEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;

import javax.swing.*;

/**
 * Date: May 24, 2005
 * Time: 7:08:55 PM
 *
 * @author Eitan Suez
 */
public class LogoUserType extends ImgEOUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof Logo))) return false;
      return ((Logo) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      ImageIcon value = (ImageIcon) Hibernate.SERIALIZABLE.nullSafeGet(rs, names[0]);
      return (value==null) ? new Logo() : new Logo(value);
   }

   public Class returnedClass() { return Logo.class; }

}
