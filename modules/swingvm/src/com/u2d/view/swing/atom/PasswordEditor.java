package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.Password;
import com.u2d.ui.MyPasswordField;
import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 1:36:37 PM
 *
 * @author Eitan Suez
 */
public class PasswordEditor extends JPanel implements AtomicEditor
{
   private JPasswordField _pf1, _pf2;

   public PasswordEditor()
   {
      setLayout(new GridLayout(0, 1));
      _pf1 = new MyPasswordField(12); add(_pf1);
      _pf2 = new MyPasswordField(12); add(_pf2);
   }

   public void render(AtomicEObject value)
   {
      _pf1.setText(""); _pf2.setText("");
   }

   public int bind(AtomicEObject value)
   {
      Password eo = (Password) value;

      String pwd1 = new String(_pf1.getPassword());
      String pwd2 = new String(_pf2.getPassword());

      if (pwd1.length() < Password.MINLENGTH)
      {
         String msg = String.format(ComplexType.localeLookupStatic("password.minlength.msg"), Password.MINLENGTH);
         eo.fireValidationException(msg);
         return 1;
      }

      if (! pwd1.equals(pwd2) )
      {
         eo.fireValidationException(ComplexType.localeLookupStatic("passwords.match.msg"));
         return 1;
      }

      eo.parseValue(pwd1);
      return 0;
   }

   public void passivate() { }

}
