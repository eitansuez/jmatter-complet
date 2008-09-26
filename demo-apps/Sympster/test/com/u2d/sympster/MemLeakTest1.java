package com.u2d.sympster;

import com.u2d.view.swing.TopLevelFormView;
import com.u2d.model.ComplexType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Sep 26, 2008
 * Time: 11:09:54 AM
 */
public class MemLeakTest1 implements Runnable
{
   private JPanel p;
   private City city;
   private TopLevelFormView formView;

   public MemLeakTest1()
   {
      new ClassPathXmlApplicationContext("applicationContext.xml");
      
      city = (City) ComplexType.forClass(City.class).instance();
      city.getName().setValue("Stockholm");
   }

   public void run()
   {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      formView = new TopLevelFormView(city);

      p = new JPanel(new BorderLayout());
      p.add(formView, BorderLayout.CENTER);
      JButton detachBtn = new JButton("Detach");
      detachBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            formView.detach();
         }
      });
      JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      btnPnl.add(detachBtn);
      JButton removeBtn = new JButton("Remove FormView");
      removeBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            formView.detach();
            p.remove(formView);
            formView = null;
            p.revalidate();
            p.repaint();
         }
      });
      btnPnl.add(removeBtn);

      p.add(btnPnl, BorderLayout.SOUTH);

      f.setContentPane(p);
      
      f.setBounds(100,100,500,500);
      f.setVisible(true);
   }

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new MemLeakTest1());
   }
}
