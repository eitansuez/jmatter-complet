package com.u2d.view.swing;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Locale;
import com.u2d.app.*;
import com.u2d.ui.*;
import com.u2d.model.ComplexType;
import com.u2d.css4swing.style.ComponentStyle;
import org.jdesktop.swingx.JXPanel;
import net.miginfocom.swing.MigLayout;

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
      applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
      setFocusCycleRoot(true);
      new MovableSupport(this);
   }
   public void setAuthMgr(AuthManager authMgr)
   {
      _authMgr = authMgr;
   }
   
   private String lookup(String key) { return ComplexType.localeLookupStatic(key); }

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
      setLayout(new MigLayout("fill, flowy"));

      JPanel topPnl = new JPanel(new MigLayout("fill", "[grow][]", "[align bottom]"));
      topPnl.setOpaque(false);
      topPnl.setMinimumSize(new Dimension(335, 30));
      ComponentStyle.addClass(topPnl, "top");
      _msg = new JLabel("Please log in..");
      ComponentStyle.addClass(_msg, "title");
      topPnl.add(_msg);
      topPnl.add(new JLabel(LOGIN_ICON));

      JPanel innerPnl = new JPanel(new MigLayout("fill, wrap 2", "[trailing][pref]"));
      innerPnl.setOpaque(false);
      ComponentStyle.setIdent(innerPnl, "login-innerpnl");

      JLabel label = new LabelWithMnemonic("logindlg.lbl.username");
      label.setLabelFor(_userNameFld);
      innerPnl.add(label);
      innerPnl.add(_userNameFld);

      label = new LabelWithMnemonic("logindlg.lbl.pwd");
      label.setLabelFor(_pwdField);
      innerPnl.add(label);
      innerPnl.add(_pwdField);

      KeycapsDetector detector = new KeycapsDetector(_pwdField);
      innerPnl.add(detector, "span, alignx leading");

      add(topPnl, "grow");
      add(innerPnl, "align center");
      add(okBtn(), "tag ok, alignx trailing");
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
      // the positioning sometimes occurs before the desktop pane is properly
      // realized/sized;  so..
      getParent().addComponentListener(new ComponentAdapter()
      {
         public void componentResized(ComponentEvent e)
         {
            if (getComponentOrientation().isLeftToRight())
            {
               setLocation(new Point(10, UIUtils.computeCenter(LoginDialog.this).y));
            }
            else
            {
               int x = LoginDialog.this.getParent().getWidth() - LoginDialog.this.getWidth() - 10 ;
               setLocation(new Point(x, UIUtils.computeCenter(LoginDialog.this).y));
            }
            LoginDialog.this.getParent().removeComponentListener(this);  // one time.
         }
      });
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
