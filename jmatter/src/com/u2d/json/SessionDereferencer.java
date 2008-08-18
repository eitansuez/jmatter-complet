package com.u2d.json;

import com.u2d.model.ComplexEObject;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 14, 2008
 * Time: 11:08:43 AM
 */
public class SessionDereferencer implements Dereferencer
{
   private Session session;

   public SessionDereferencer(Session session)
   {
      this.session = session;
   }

   public ComplexEObject get(Class cls, Long id)
   {
      return (ComplexEObject) session.get(cls, id);
   }
}
