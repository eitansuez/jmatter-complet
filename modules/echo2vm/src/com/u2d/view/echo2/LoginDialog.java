package com.u2d.view.echo2;

import nextapp.echo2.app.*;
import nextapp.echo2.app.layout.RowLayoutData;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ActionEvent;
import com.u2d.app.AuthManager;
import com.u2d.model.ComplexType;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 2, 2007
 * Time: 2:47:10 PM
 */
public class LoginDialog extends WindowPane
{

   private TextField _userNameFld = null;
   private PasswordField _pwdField = null;
   private Label _msg = null;
   private AuthManager _authMgr;
   
   public LoginDialog(AuthManager authMgr)
   {
      super();
      String title = lookup("logindlg.title");
      if (title == null) title = "Login";
      setTitle(title);
      setResizable(false);
      setClosable(false);
      setMovable(true);
      _authMgr = authMgr;

      setDefaultCloseOperation(HIDE_ON_CLOSE);

      setBackground(new Color(0xffffff));
      setupFields();
      layItOut();
   }
   
   private String lookup(String key)
   {
      return ComplexType.localeLookupStatic(key);
   }

   private void setupFields()
   {
      _userNameFld = new TextField();
      _pwdField = new PasswordField();
   }

   private void layItOut()
   {
      _msg = new Label("Please log in..");

      Column c = new Column();
      Row row = new Row();
      row.add(_msg);
      Label iconLabel = new Label(Echo2ViewMechanism.imageFor(LOGIN_ICON));
      RowLayoutData data = new RowLayoutData();
      data.setAlignment(new Alignment(Alignment.RIGHT, Alignment.DEFAULT));
      iconLabel.setLayoutData(data);
      row.add(iconLabel);
      c.add(row);
      

      row = new Row();
      Label label = new Label(lookup("logindlg.lbl.username"));
//      label.setLabelFor(_userNameFld);
      row.add(label);
      row.add(_userNameFld);
      c.add(row);
      
      row = new Row();
      label = new Label(lookup("logindlg.lbl.pwd"));
//      label.setLabelFor(_pwdField);
      row.add(label);
      row.add(_pwdField);
      c.add(row);
      
      Button btn = okBtn();
      data = new RowLayoutData();
      data.setAlignment(new Alignment(Alignment.RIGHT,  Alignment.DEFAULT));
      btn.setLayoutData(data);
      row = new Row();
      row.add(btn);
      c.add(row);
      
      add(c);
   }
   
   private Button okBtn()
   {
      Button okBtn = new Button("OK");

      okBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            final String username = _userNameFld.getText();
            final String pwd = _pwdField.getText();
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
//      _userNameFld.requestFocusInWindow();
   }
   

}
