package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 11, 2006
 * Time: 10:11:15 PM
 */
public class MnemonicTest extends JFrame
{
   public MnemonicTest()
   {
      super("Mnenomic Test");
      JPanel p = (JPanel) getContentPane();
      p.setLayout(new FlowLayout(FlowLayout.LEFT));
      
      JLabel label = new JLabel("<html>One</html>");
      JTextField tf = new JTextField(12);
      label.setLabelFor(tf);
      char mn = 'o';
      label.setDisplayedMnemonic(mn);
      String text = label.getText();
      label.setDisplayedMnemonicIndex(text.indexOf(mn));
      
      p.add(label);
      p.add(tf);
      p.add(new JTextField(15));
   }
   public static void main(String[] args)
   {
      JFrame f = new MnemonicTest();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocation(100,100);
      f.pack();
      f.setVisible(true);
   }

}
