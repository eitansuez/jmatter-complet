package com.u2d.view.swing.atom;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 6, 2008
 * Time: 2:47:10 PM
 */
public class MaxLengthTest extends JPanel
{
   public MaxLengthTest()
   {
      JTextField tf = new JTextField();
      tf.setColumns(20);
      tf.setDocument(new MaxLength(10));
      add(tf);
   }
   public static void main(String[] args)
   {
      JFrame f = new JFrame("Max length test..");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setContentPane(new MaxLengthTest());
      f.setBounds(100,100,500,500);
      f.setVisible(true);
   }
}

