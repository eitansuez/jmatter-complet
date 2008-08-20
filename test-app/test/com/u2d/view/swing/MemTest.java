package com.u2d.view.swing;

import com.u2d.view.EView;
import com.u2d.model.ComplexType;
import com.u2d.domain.Meeting;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 11, 2008
 * Time: 12:57:41 PM
 */
public class MemTest extends JPanel
{
   EView view;
   
   public MemTest()
   {
      setLayout(new BorderLayout());

      Meeting meeting = (Meeting) ComplexType.forClass(Meeting.class).instance();
      view = meeting.getMainView();
      add((JComponent) view, BorderLayout.CENTER);

      JButton btn = new JButton("Test");
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            TestableCommandAdapter.count();
            view.detach();
            TestableCommandAdapter.count();
//            for (Exception ex : CommandAdapter.constrTraces())
//            {
//               ex.printStackTrace();
//            }
         }
      });

      add(btn, BorderLayout.PAGE_END);
   }
   public static void main(String[] args)
   {
      new ClassPathXmlApplicationContext("applicationContext.xml");

      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      f.setContentPane(new MemTest());
      f.setBounds(100,100,500,500);
      f.setVisible(true);
   }
}
