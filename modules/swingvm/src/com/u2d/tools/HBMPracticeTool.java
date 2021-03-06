/*
 * Created on Sep 7, 2004
 */
package com.u2d.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.u2d.ui.MessagePanel;
import com.u2d.ui.MyTextArea;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.view.swing.FlexiFrame;
import com.u2d.view.swing.AppLoader;
import com.u2d.app.*;
import com.u2d.list.PlainListEObject;
import com.u2d.model.ComplexEObject;
import com.u2d.persist.HBMSingleSession;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Eitan Suez
 */
public class HBMPracticeTool extends JFrame
{
   EODesktopPane _desktopPane;
   HBMPersistenceMechanism _pmech;
   MessagePanel _msgPnl;

   public HBMPracticeTool()
   {
      ApplicationContext context =
            new ClassPathXmlApplicationContext("applicationContext.xml");

      _pmech = (HBMPersistenceMechanism) context.getBean("persistor");

      // hbm initialization takes place in Application constructor
      // Persisted class list read from app.properties

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      JPanel contentPane = (JPanel) getContentPane();
      contentPane.setLayout(new BorderLayout());

      contentPane.add(new HBMQueryPanel(), BorderLayout.PAGE_START);
      _desktopPane = new EODesktopPane();
      contentPane.add(_desktopPane, BorderLayout.CENTER);
      _msgPnl = new MessagePanel();
      contentPane.add(_msgPnl, BorderLayout.PAGE_END);
   }

   class HBMQueryPanel extends JPanel
   {
      private JTextArea _queryArea;
      HBMQueryPanel()
      {
         setLayout(new BorderLayout());
         _queryArea = queryTextArea();
         add(_queryArea, BorderLayout.CENTER);
         add(submitQueryBtn(), BorderLayout.LINE_END);

         setBorder(BorderFactory.createTitledBorder("Query"));
      }

      private JTextArea queryTextArea()
      {
         return new MyTextArea(3, 60);
      }

      private JButton submitQueryBtn()
      {
         JButton btn = new JButton("Submit Query");
         btn.addActionListener(new ActionListener()
               {
            public void actionPerformed(ActionEvent evt)
            {
               AppLoader.getInstance().newThread(new Runnable()
               {
                  public void run()
                  {
                     try
                     {
                        String hql = clean(_queryArea.getText());

                        Session session = _pmech.getSession();
                        final java.util.List results = session.createQuery(hql).list();

                        SwingUtilities.invokeLater( new Runnable()
                        {
            public void run()
            {
               if (results.isEmpty())
               {
                  _msgPnl.message("Empty Result Set");
               }
               else
               {
                  Object first = results.iterator().next();
                  EView view = null;
                  if (results.size() == 1)
                  {
                     ComplexEObject result = (ComplexEObject) results.get(0);
                     result.onLoad();
                     view = result.getFormView();
                  }
                  else
                  {
                     PlainListEObject leo = new PlainListEObject(first.getClass(), results);
                     view = leo.getTableView();
                  }
                  FlexiFrame frame = new FlexiFrame((JComponent) view);
                  _desktopPane.addFrame(frame);
               }
            }
                        });

                     }
                     catch (HibernateException ex)
                     {
                        System.err.println("Hibernate Exception: "+ex.getMessage());
                        ex.printStackTrace();
                        _msgPnl.message(ex.getMessage());

                        if (_pmech instanceof HBMSingleSession)
                        {
                           ((HBMSingleSession) _pmech).newSession();
                        }
                     }
                  }
               }).start();

            }
               });
         return btn;
      }
   }

   private String clean(String hql)
   {
      if (hql.endsWith(";"))
      {
         return hql.substring(0, hql.length() - 1);
      }
      return hql;
   }

   public static void main(String[] args)
   {
      JFrame f = new HBMPracticeTool();
      f.setLocation(100,100);
      f.pack();
      f.setVisible(true);
   }
}
