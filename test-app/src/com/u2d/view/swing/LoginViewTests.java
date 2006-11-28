/*
 * Created on Dec 15, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import com.u2d.ui.KeycapsDetector;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Testing various layouts for a login panel..not "really" a test
 * 
 * @author Eitan Suez
 */
public class LoginViewTests
{
   public static void main(String[] args)
   {
      new LoginViewTests();
   }

   public LoginViewTests()
   {
      try
      {
         UIManager
               .setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
      }
      catch (Exception e)
      {
      }
      JFrame f = new JFrame("Login View Tests");
      JPanel p = (JPanel) f.getContentPane();
      p.setLayout(new FlowLayout());

      JPanel first = new JPanel();
      buildLoginPanel(first);
      p.add(first);

      JPanel second = new JPanel();
      buildLoginPanel2(second);
      p.add(second);

      JPanel third = new JPanel();
      buildLoginPanel3(third);
      p.add(third);

      JPanel fourth = new JPanel();
      buildLoginPanel4(fourth);
      p.add(fourth);

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
   }

   private void buildLoginPanel(JPanel p)
   {
      FormLayout layout = new FormLayout("15px, right:pref, 5px, pref", "");
      DefaultFormBuilder builder = new DefaultFormBuilder(layout, p);
      builder.setDefaultDialogBorder();
      builder.setLeadingColumnOffset(1);

      builder.appendSeparator("Please Log In");
      builder.appendRow("5px");
      builder.nextLine(2);

      builder.append("User &Name:", new JTextField(12));
      builder.nextLine();
      builder.append("&Password:", new JPasswordField(12));

      builder.appendRow("10px");
      builder.nextLine(2);
      builder.append(ButtonBarFactory.buildOKBar(new JButton("OK")), 3);
   }

   private void buildLoginPanel2(JPanel p)
   {
      FormLayout layout = new FormLayout("15px, right:pref, 5px, pref",
            "pref, top:21px, pref, 7px, pref, 21px, pref");
      p.setLayout(new BorderLayout());
      
      DefaultFormBuilder builder = new DefaultFormBuilder(layout);
      builder.setDefaultDialogBorder();
      CellConstraints cc = new CellConstraints();

      builder.addTitle("Please Log In", cc.xyw(1, 1, 4));
      builder.addSeparator("", cc.xyw(1, 2, 4));
      
      builder.addLabel("User &Name:", cc.xy(2, 3));
      builder.add(new JTextField(10), cc.xy(4, 3));
      builder.addLabel("&Password:", cc.xy(2, 5));
      JPasswordField pwd = new JPasswordField(12);
      builder.add(pwd, cc.xy(4, 5));

      ButtonBarBuilder barBuilder = new ButtonBarBuilder();
      barBuilder.addFixedNarrow(new KeycapsDetector(pwd));
      barBuilder.addGlue();
      barBuilder.addFixed(new JButton("OK"));
      
      p.add(builder.getPanel(), BorderLayout.CENTER);
      p.add(barBuilder.getPanel(), BorderLayout.SOUTH);
   }

   private void buildLoginPanel3(JPanel p)
   {
      final JLabel msg =
            DefaultComponentFactory.getInstance().createTitle("Please Log In..");
      Icon icon = LoginDialog.LOGIN_ICON;
      JTextField userName = new JTextField(12);
      userName.setColumns(12);
      JPasswordField pwd = new JPasswordField(12);
      KeycapsDetector detector = new KeycapsDetector(pwd);
      JButton btn = new JButton("OK");
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            msg.setText("Let's try changing the length of this message..");
         }
      });

      FormLayout layout = new FormLayout(
            "15px, right:pref:grow, 5px, left:pref:grow, pref",
            "pref, top:15px, pref, 5px, pref, 5px, 10px, pref, pref");
      DefaultFormBuilder builder = new DefaultFormBuilder(layout, p);
      builder.setDefaultDialogBorder();
      builder.setLeadingColumnOffset(1);

      CellConstraints cc = new CellConstraints();

      builder.add(msg, cc.xyw(1, 1, 4, "fill, bottom"));
      builder.add(new JLabel(icon), cc.xy(5, 1));

      builder.addSeparator("", cc.xyw(1, 2, 5));

      builder.addLabel("User &Name:", cc.xy(2, 3));
      builder.add(userName, cc.xyw(4, 3, 2));
      builder.addLabel("&Password:", cc.xy(2, 5));
      builder.add(pwd, cc.xyw(4, 5, 2));

      builder.addSeparator("", cc.xyw(1, 7, 5));
      builder.add(detector, cc.xyw(2, 8, 3));


      builder.add(ButtonBarFactory.buildOKBar(btn), cc.xy(5, 9));

   }

   private void buildLoginPanel4(JPanel p)
   {
      final JLabel msg =
            DefaultComponentFactory.getInstance().createTitle("Please Log In..");
//      Icon icon = LoginDialog.LOGIN_ICON;
      JTextField userName = new JTextField(12);
      userName.setColumns(12);
      JPasswordField pwd = new JPasswordField(12);
      KeycapsDetector detector = new KeycapsDetector(pwd);
      JButton btn = new JButton("OK");
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            msg.setText("Let's try changing the length of this message..");
         }
      });

      FormLayout layout = new FormLayout(
            "left:pref:grow, 5px, pref",
            "bottom:pref, top:15px, center:pref, 5px, pref");

      DefaultFormBuilder builder = new DefaultFormBuilder(layout, p);
      builder.setDefaultDialogBorder();
      builder.setLeadingColumnOffset(1);

      CellConstraints cc = new CellConstraints();

      builder.add(msg, cc.xy(1, 1));
      builder.add(new JLabel(LoginDialog.LOGIN_ICON), cc.xy(3, 1));

      builder.addSeparator("", cc.xyw(1, 2, 3));
      builder.addSeparator("", cc.xyw(1, 4, 3));

      JPanel bar = ButtonBarFactory.buildOKBar(btn);
      bar.setOpaque(false);
      builder.add(bar, cc.xy(3, 5));


      FormLayout innerLayout = new FormLayout(
            "right:pref, 5px, pref", "pref, 5px, pref, 5px, pref");
      DefaultFormBuilder innerBuilder = new DefaultFormBuilder(innerLayout);


      innerBuilder.addLabel("User &Name:", cc.xy(1, 1));
      innerBuilder.add(userName, cc.xy(3, 1));
      innerBuilder.addLabel("&Password:", cc.xy(1, 3));
      innerBuilder.add(pwd, cc.xy(3, 3));
      innerBuilder.add(detector, cc.xyw(1, 5, 3));
      JPanel innerPnl = innerBuilder.getPanel();
      innerPnl.setOpaque(false);

      builder.add(innerPnl, cc.xyw(1, 3, 3, "center, center"));
   }

}