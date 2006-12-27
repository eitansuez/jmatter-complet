package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.Password;
import org.wings.SPanel;
import org.wings.SPasswordField;
import org.wings.SGridLayout;

/**
 * Date: Jun 8, 2005
 * Time: 1:36:37 PM
 *
 * @author Eitan Suez
 */
public class PasswordEditor extends SPanel implements AtomicEditor
{
   private SPasswordField _pf1, _pf2;

   public PasswordEditor()
   {
      setLayout(new SGridLayout(0, 1));
      _pf1 = new SPasswordField(); _pf1.setColumns(12); add(_pf1);
      _pf2 = new SPasswordField(); _pf2.setColumns(12); add(_pf2);
   }

   public void render(AtomicEObject value)
   {
      _pf1.setText(""); _pf2.setText("");
   }

   public int bind(AtomicEObject value)
   {
      Password eo = (Password) value;

      String pwd1 = new String(_pf1.getText());
      String pwd2 = new String(_pf2.getText());

      if (pwd1.length() < Password.MINLENGTH)
      {
         eo.fireValidationException("Password must be at least "+Password.MINLENGTH+" characters long");
         return 1;
      }

      if (! pwd1.equals(pwd2) )
      {
         eo.fireValidationException("Password and repeated password do not match.");
         return 1;
      }

      eo.parseValue(pwd1);
      return 0;
   }

   public void passivate() { }

}
