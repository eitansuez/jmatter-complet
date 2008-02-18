package com.u2d.view.swing;

import javax.swing.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.factories.ButtonBarFactory;
import java.awt.event.*;
import java.awt.*;

import com.u2d.app.*;
import com.u2d.ui.*;
import com.u2d.model.ComplexType;
import com.u2d.css4swing.style.ComponentStyle;
import org.jdesktop.swingx.JXPanel;

/**
 * @author Eitan Suez
 */
public class LoginDialog extends JXPanel
{
   private JTextField _userNameFld = null;
   private JPasswordField _pwdField = null;
   private JLabel _msg = null;
   private AuthManager _authMgr;
   
   public LoginDialog(AuthManager authMgr)
   {
      super();
      _authMgr = authMgr;
      setupFields();
      layItOut();
      setFocusCycleRoot(true);
      new MovableSupport(this);
   }
   public void setAuthMgr(AuthManager authMgr)
   {
      _authMgr = authMgr;
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
      _msg = new JLabel("Please log in..");
      ComponentStyle.addClass(_msg, "title");
      KeycapsDetector detector = new KeycapsDetector(_pwdField);

      FormLayout layout = new FormLayout(
            "left:pref:grow, 5px, right:pref",
            "bottom:pref, center:pref, pref");

      DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
      builder.setDefaultDialogBorder();
      builder.setLeadingColumnOffset(1);

      CellConstraints cc = new CellConstraints();

      builder.add(_msg, cc.rc(1, 1));
      builder.add(new JLabel(LOGIN_ICON), cc.rc(1, 3));

      JPanel bar = ButtonBarFactory.buildOKBar(okBtn());
      bar.setOpaque(false);
      builder.add(bar, cc.rc(3, 3));


      FormLayout innerLayout = new FormLayout("right:pref, 5px, pref",
                                              "pref, 5px, pref, 5px, pref");
      DefaultFormBuilder innerBuilder = new DefaultFormBuilder(innerLayout);
      
      JLabel label = innerBuilder.addLabel(lookup("logindlg.lbl.username"), cc.rc(1, 1));
      label.setLabelFor(_userNameFld);
      innerBuilder.add(_userNameFld, cc.rc(1, 3));
      
      label = innerBuilder.addLabel(lookup("logindlg.lbl.pwd"), cc.rc(3, 1));
      label.setLabelFor(_pwdField);
      innerBuilder.add(_pwdField, cc.rc(3, 3));
      
      innerBuilder.add(detector, cc.rcw(5, 1, 3));
      JPanel innerPnl = innerBuilder.getPanel();
      ComponentStyle.addClass(innerPnl, "login-innerpnl");
      innerPnl.setOpaque(false);

      builder.add(innerPnl, cc.rcw(2, 1, 3, "center, center"));
   }
   
   private JButton okBtn()
   {
      JButton okBtn = new DefaultButton("OK");
      ComponentStyle.setIdent(okBtn, "login-okbtn");

      okBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            final String username = _userNameFld.getText();
            final String pwd = new String(_pwdField.getPassword());
            AppLoader.getInstance().newThread(new Runnable() {
               public void run() {
                  _authMgr.onLogin(username, pwd);
               }
            }).start();
         }
      });
      
      return okBtn;
   }
   
   static Icon LOGIN_ICON_SMALL, LOGIN_ICON;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/login16.png");
      LOGIN_ICON_SMALL = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/login32.png");
      LOGIN_ICON = new ImageIcon(imgURL);
   }
   
   private void reset(String msg)
   {
      _userNameFld.setText("");
      _pwdField.setText("");
      _msg.setText(msg);
      setSize(getPreferredSize());
      getfocus();
   }
   
   public void position() {
      ((JComponent) this.getParent()).revalidate();
      setSize(getPreferredSize());
      Point p = new Point(10, UIUtils.computeCenter(this).y);
      setLocation(p);  // left align..
   }
   
   public void clear() { reset(lookup("logindlg.msg.login")); }
   public void loginInvalid() { reset(lookup("logindlg.msg.failed_auth")); }
   public void userLocked() { reset(lookup("logindlg.msg.user_locked")); }
   
   private void getfocus()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _userNameFld.requestFocusInWindow();
         }
      });
   }
   
}
