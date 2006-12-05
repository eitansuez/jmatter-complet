package com.u2d.persist.type;

import com.u2d.type.atom.ColorEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 4, 2006
 * Time: 9:21:32 PM
 */
public class ColorEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof ColorEO))) return false;
      return ((ColorEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new ColorEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      Color c = (value==null) ? null : ((ColorEO) value).colorValue();
      String textValue = Integer.toHexString(c.getRGB());
      Hibernate.STRING.nullSafeSet(pstmt, textValue, index);
   }

   public Class returnedClass() { return ColorEO.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

}
