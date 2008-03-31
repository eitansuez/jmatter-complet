package com.u2d.view.swing.list;

import com.u2d.list.CompositeList;
import com.u2d.type.composite.Name;
import com.u2d.app.Application;
import javax.swing.*;
import java.awt.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Mar 28, 2008
 * Time: 4:09:06 PM
 */
public class TabularViewTest extends JPanel
{
   public TabularViewTest()
   {
      CompositeList list = new CompositeList(Name.class);
      list.add(new Name("Eitan", "Suez"));
      list.add(new Name("Leslie", "Suez"));
      list.add(new Name("Maia", "Suez"));
      list.add(new Name("Arik", "Suez"));
      list.add(new Name("Ezra", "Suez"));

      CompositeTabularView view = new CompositeTabularView(list);
      setLayout(new BorderLayout());
      add(view, BorderLayout.CENTER);
   }

   public static void main(String[] args)
   {
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      Application app = (Application) context.getBean("application");
      app.postInitialize();

      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setBounds(100,100,500,500);
      f.setContentPane(new TabularViewTest());
      f.setVisible(true);
   }
}
