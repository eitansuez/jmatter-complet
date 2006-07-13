package com.u2d.pattern;

import com.u2d.app.PersistenceMechanism;
import com.u2d.app.AppFactory;
import com.u2d.app.Tracing;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;

/**
 * Date: May 25, 2005
 * Time: 1:44:27 PM
 *
 * @author Eitan Suez
 */
public class SingletonHelper
{
   public static Singleton getInstance(Class clazz)
   {
      PersistenceMechanism persistor = AppFactory.getInstance().getApp().getPersistenceMechanism();
      ComplexEObject item = persistor.fetchSingle(clazz);

      if (item /* still */ == null)
      {
         item = makeInstance(clazz);
         Tracing.tracer().config("Created "+clazz.getName()+" singleton: "+item.title());
      }
      else
      {
         Tracing.tracer().config("Fetched "+clazz.getName()+" singleton: "+item.title());
      }
      return (Singleton) item;
   }

   private static ComplexEObject makeInstance(Class clazz)
   {
      ComplexType type = ComplexType.forClass(clazz);
      ComplexEObject item = type.New(null);
      item.save();
      return item;
   }

}
