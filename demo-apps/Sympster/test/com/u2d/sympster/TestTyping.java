package com.u2d.sympster;

import junit.framework.TestCase;
import java.lang.reflect.Method;
import org.hibernate.repackage.cglib.proxy.Enhancer;
import org.hibernate.repackage.cglib.proxy.MethodInterceptor;
import org.hibernate.repackage.cglib.proxy.MethodProxy;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Jun 6, 2008
 * Time: 4:21:36 PM
 */
public class TestTyping extends TestCase
{
   public void testTyping()
   {
      assertTrue(Venue.class.isAssignableFrom(Hotel.class));

      Enhancer e = new Enhancer();
      e.setSuperclass(Venue.class);
      e.setCallback(new MethodInterceptor()
      {
         public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable
         {
            return proxy.invokeSuper(o, args);
         }
      });
      Object bean = e.create();
      System.out.println("bean class: "+bean.getClass());

      assertFalse(bean.getClass().isAssignableFrom(Hotel.class));

      assertTrue(Enhancer.isEnhanced(bean.getClass()));

      Class actualClass = bean.getClass().getSuperclass();
      assertEquals(actualClass, Venue.class);
      assertTrue(actualClass.isAssignableFrom(Hotel.class));

   }
}
