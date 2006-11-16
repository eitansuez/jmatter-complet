package com.u2d.contactmgr;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 15, 2006
 * Time: 2:17:50 PM
 */

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.u2d.element.Command;
import com.u2d.element.OverloadedEOCmd;
import com.u2d.model.ComplexType;
import com.u2d.type.composite.Person;

public class CommandTest
      extends TestCase
{
   
   public void testPersistOverloaded()
   {
//      ApplicationContext context = 
//            new ClassPathXmlApplicationContext("applicationContext.xml");
      
      ComplexType personType = ComplexType.forClass(Person.class);
      Command newCmd = personType.command("New");
      assertTrue(newCmd instanceof OverloadedEOCmd);
      
      assertEquals("com.u2d.type.composite.Person#New", newCmd.fullPath());
      
//      newCmd.save();
   }
   
}