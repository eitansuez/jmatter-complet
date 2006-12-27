package com.u2d.view.wings;

import com.u2d.app.AuthManager;
import com.u2d.model.ComplexType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.wings.*;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Color;
import java.awt.GridBagConstraints;

/**
 * @author Eitan Suez
 */
public class LoginDialog
      extends SInternalFrame
{
   private STextField _userNameFld = null;
   private SPasswordField _pwdField = null;
   private SLabel _msg = null;
   private AuthManager _authMgr;
   
   public LoginDialog(AuthManager authMgr)
   {
      super();
      setIconifyable(false);
      setClosable(false);
      setMaximizable(false);
      setTitle(lookup("logindlg.title"));
      _authMgr = authMgr;

      setIcon(new SImageIcon((ImageIcon) LOGIN_ICON_SMALL));

      SPanel p = (SPanel) getContentPane();
      p.setBackground(new Color(0xbbffffff, true));

      setupFields();

      layItOut();
   }
   
   private String lookup(String key)
   {
      String text = ComplexType.localeLookupStatic(key);
      return text.replaceAll("&", "");
   }

   private void setupFields()
   {
      _userNameFld = new STextField();
      _userNameFld.setColumns(12);
//      _userNameFld.addFocusListener(new FocusListener()
//         {
//            public void focusGained(FocusEvent evt)
//            {
//               _userNameFld.selectAll();
//            }
//            public void focusLost(FocusEvent evt) {}
//         });

      _pwdField = new SPasswordField();
      _pwdField.setColumns(12);
   }

   private void layItOut()
   {
      SPanel p = (SPanel) getContentPane();
      SForm form = new SForm();
      p.add(form, SBorderLayout.CENTER);
      
      SPanel topPanel = new SPanel(new SBorderLayout());
      _msg = new SLabel("Please log in..");
      topPanel.add(_msg, SBorderLayout.WEST);
      topPanel.add(new SLabel(new SImageIcon((ImageIcon) LOGIN_ICON)), SBorderLayout.EAST);
      
      SPanel bottomPnl = new SPanel(new SFlowLayout(SConstants.RIGHT_ALIGN));
      bottomPnl.add(okBtn());
      
      form.add(topPanel, SBorderLayout.NORTH);
      form.add(loginPnl(), SBorderLayout.CENTER);
      form.add(bottomPnl, SBorderLayout.SOUTH);
   }

   private SComponent loginPnl()
   {
      SPanel loginPnl = new SPanel(new SGridBagLayout());
      GridBagConstraints cc = new GridBagConstraints();
      
      cc.gridy = 0;
      cc.gridx = 0;
      cc.gridwidth = 1;
      cc.ipadx = 5;
      cc.anchor = GridBagConstraints.EAST;
      SLabel label = new SLabel(lookup("logindlg.lbl.username"));
      loginPnl.add(label, cc);
      
      cc.gridx = 1;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      cc.anchor = GridBagConstraints.WEST;
      loginPnl.add(_userNameFld, cc);
      
      
      cc.gridy = 1;
      cc.gridx = 0;
      cc.gridwidth = 1;
      cc.ipadx = 5;
      cc.anchor = GridBagConstraints.EAST;
      label = new SLabel(lookup("logindlg.lbl.pwd"));
      loginPnl.add(label, cc);

      cc.gridx = 1;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      cc.anchor = GridBagConstraints.WEST;
      loginPnl.add(_pwdField, cc);
      
      return loginPnl;
   }

   private SButton okBtn()
   {
      SButton okBtn = new DefaultButton("OK");

      okBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            String username = _userNameFld.getText();
            String pwd = _pwdField.getText();
            _authMgr.onLogin(username, pwd);
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
   }
   
   public void clear() { reset(lookup("logindlg.msg.login")); }
   public void loginInvalid() { reset(lookup("logindlg.msg.failed_auth")); }
   public void userLocked() { reset(lookup("logindlg.msg.user_locked")); }
   
   public void makeVisible()
   {
      setVisible(true);
      _userNameFld.requestFocus();
//      _userNameFld.requestFocusInWindow();
   }
   
}
