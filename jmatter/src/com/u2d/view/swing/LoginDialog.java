package com.u2d.view.swing;

import javax.swing.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.ButtonBarFactory;
import java.awt.event.*;
import java.awt.*;
import com.u2d.app.*;
import com.u2d.ui.*;
import com.u2d.model.ComplexType;

/**
 * @author Eitan Suez
 */
public class LoginDialog extends JInternalFrame
{
   private JTextField _userNameFld = null;
   private JPasswordField _pwdField = null;
   private JLabel _msg = null;
   private AuthManager _authMgr;
   
   public LoginDialog(AuthManager authMgr)
   {
      super("Login", false, false, false, false);
      setTitle(lookup("logindlg.title"));
      _authMgr = authMgr;

      setFrameIcon(LOGIN_ICON_SMALL);
      setDefaultCloseOperation(HIDE_ON_CLOSE);

      JPanel p = (JPanel) getContentPane();
      p.setOpaque(true);
      p.setBackground(new Color(0xbbffffff, true));

      setupFields();

      layItOut();
      pack();
   }
   
   private String lookup(String key)
   {
      return ComplexType.localeLookupStatic(key);
   }

   private void setupFields()
   {
      _userNameFld = new JTextField(12);
      _userNameFld.addFocusListener(new FocusListener()
         {
            public void focusGained(FocusEvent evt)
            {
               _userNameFld.selectAll();
            }
            public void focusLost(FocusEvent evt) {}
         });

      _pwdField = new MyPasswordField(12);
   }

   private void layItOut()
   {
      JPanel p = (JPanel) getContentPane();
      _msg = DefaultComponentFactory.getInstance().createTitle("Please log in..");
      KeycapsDetector detector = new KeycapsDetector(_pwdField);

      FormLayout layout = new FormLayout(
            "left:pref:grow, 5px, pref",
            "bottom:pref, top:15px, center:pref, 5px, pref");

      DefaultFormBuilder builder = new DefaultFormBuilder(layout, p);
      builder.setDefaultDialogBorder();
      builder.setLeadingColumnOffset(1);

      CellConstraints cc = new CellConstraints();

      builder.add(_msg, cc.xy(1, 1));
      builder.add(new JLabel(LOGIN_ICON), cc.xy(3, 1));

      builder.addSeparator("", cc.xyw(1, 2, 3));
      builder.addSeparator("", cc.xyw(1, 4, 3));

      JPanel bar = ButtonBarFactory.buildOKBar(okBtn());
      bar.setOpaque(false);
      builder.add(bar, cc.xy(3, 5));


      FormLayout innerLayout = new FormLayout(
            "right:pref, 5px, pref", "pref, 5px, pref, 5px, pref");
      DefaultFormBuilder innerBuilder = new DefaultFormBuilder(innerLayout);


      JLabel label = innerBuilder.addLabel(lookup("logindlg.lbl.username"), cc.xy(1, 1));
      label.setLabelFor(_userNameFld);
      innerBuilder.add(_userNameFld, cc.xy(3, 1));
      
      label = innerBuilder.addLabel(lookup("logindlg.lbl.pwd"), cc.xy(1, 3));
      label.setLabelFor(_pwdField);
      innerBuilder.add(_pwdField, cc.xy(3, 3));
      
      innerBuilder.add(detector, cc.xyw(1, 5, 3));
      JPanel innerPnl = innerBuilder.getPanel();
      innerPnl.setOpaque(false);

      builder.add(innerPnl, cc.xyw(1, 3, 3, "center, center"));
   }
   
   private JButton okBtn()
   {
      JButton okBtn = new DefaultButton("OK");

      okBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            final String username = _userNameFld.getText();
            final String pwd = new String(_pwdField.getPassword());
            new Thread() { public void run() {
               _authMgr.onLogin(username, pwd);
            } }.start();
         }
      });
      
      return okBtn;
   }
   
   static Icon LOGIN_ICON_SMALL, LOGIN_ICON;
   static
   {
      ClassLoader loader = LoginDialog.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/login16.png");
      LOGIN_ICON_SMALL = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/login32.png");
      LOGIN_ICON = new ImageIcon(imgURL);
   }
   
   private void reset(String msg)
   {
      setVisible(false);
      _userNameFld.setText("");
      _pwdField.setText("");
      _msg.setText(msg);
      makeVisible();
      pack();
   }
   
   public void clear() { reset(lookup("logindlg.msg.login")); }
   public void loginInvalid() { reset(lookup("logindlg.msg.failed_auth")); }
   public void userLocked() { reset(lookup("logindlg.msg.user_locked")); }
   
   public void makeVisible()
   {
      setVisible(true);
      _userNameFld.requestFocusInWindow();
   }
   
}
