/*
 * Created on Jan 23, 2004
 */
package com.u2d.view.swing;

import junit.framework.TestCase;
import java.util.*;
import com.u2d.type.atom.*;
import com.u2d.type.composite.*;
import com.u2d.app.*;
import com.u2d.element.Command;
import com.u2d.element.Field;
import com.u2d.view.swing.atom.*;

/**
 * @author Eitan Suez
 */
public class PubSubTest extends TestCase
{

   protected void setUp() throws Exception
   {
      AppFactory.getInstance().getApp().
            getViewMechanism().launch();
   }
   
   public void testPubSub1()
   {
      StringEO eo = new StringEO("A test");
      AtomicView view = (AtomicView) eo.getView();
      //System.out.println(view.getText());
      assertEquals("values differ", "A test", view.getEObject().toString());
   }
   
   public void testPubSub2()
   {
      StringEO eo = new StringEO("A test");
      AtomicView view = (AtomicView) eo.getView();

      eo.Capitalize(null);
      
      //System.out.println(view.getText());
      assertEquals("values differ", "A TEST", view.getEObject().toString());
   }
   
   public void testPubSub3()
   {
      USAddress addr = new USAddress("9300 Axtellon Ct", "Austin", "TX", "89849");
      List fields = addr.childFields();
      Field field = null;
      for (int i=0; i<fields.size(); i++)
      {
         field = (Field) fields.get(i);
         if ("city".equals(field.getName()))
         {
            //System.out.println("Ok, found city field..");
            StringEO eo = (StringEO) field.get(addr);
            AtomicView view = (AtomicView) eo.getView();
            //System.out.println(view.getText());
            assertEquals("values differ", "Austin", view.getEObject().toString());
            eo.Capitalize(null);
            //System.out.println(view.getText());
            assertEquals("values differ", "AUSTIN", view.getEObject().toString());
         }
      }
   }
   
   public void testPubSub4() throws Exception
   {
      USAddress addr = new USAddress("9300 Axtellon Ct", "Austin", "TX", "89849");
      List fields = addr.childFields();
      Field field = null;
      for (int i=0; i<fields.size(); i++)
      {
         field = (Field) fields.get(i);
         if ("city".equals(field.getName()))
         {
            StringEO eo = (StringEO) field.get(addr);
            AtomicView view = (AtomicView) eo.getView();
            assertEquals("values differ", "Austin", view.getEObject().toString());
            //eo.commandCapitalize();
            Command command = (Command) eo.commands().iterator().next();
            //System.out.println("Command is: "+command.getName());
            command.execute(eo, view);
            assertEquals("values differ", "AUSTIN", view.getEObject().toString());
         }
      }
   }
   
}
