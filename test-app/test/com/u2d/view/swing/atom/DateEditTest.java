package com.u2d.view.swing.atom;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXFrame;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.u2d.type.atom.DateEO;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Dec 2, 2008
 * Time: 10:16:59 AM
 */
public class DateEditTest extends JXPanel
{
   public DateEditTest()
   {
      DateEO date = new DateEO();
      AtomicView dateView = (AtomicView) date.getView();
      dateView.setEditable(true);
      add(dateView);
   }

   public static void main(String[] args)
   {
      new ClassPathXmlApplicationContext("applicationContext.xml");

      JXFrame f = new JXFrame("Testing DateEditor", true);
      f.setContentPane(new DateEditTest());
      f.setSize(500,500);
      f.setLocationRelativeTo(null);
      f.setVisible(true);
   }
}
