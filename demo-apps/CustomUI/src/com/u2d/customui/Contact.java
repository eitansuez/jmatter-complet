package com.u2d.customui;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.composite.Name;
import com.u2d.persist.Persist;
import com.u2d.reflection.Cmd;
import com.u2d.element.CommandInfo;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 23, 2006
 * Time: 2:10:02 PM
 */
@Persist
public class Contact extends AbstractComplexEObject
{
   private final Name name = new Name();
   private final Address address = new Address();

   public static String[] fieldOrder = {"name", "address"};
   public static String[] tabViews = {"address"};
   public static final String[] flattenIntoParent = {"name"};

   public Contact() {}

   public void initialize() { address.initialize(); }

   public Name getName() { return name; }
   public Address getAddress() { return address; }
   
   @Cmd(mnemonic='c')
   public JComponent CustomJComponent(CommandInfo cmdInfo)
   {
      JButton btn = new JButton("Say Hello!");
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            JOptionPane.showMessageDialog(null, "hello!");
         }
      });
      return btn;
   }
   

   public Title title() { return getName().title(); }

}
