/*
 * Created on Feb 9, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import com.u2d.ui.desktop.CloseableJInternalFrame;

/**
 * @author Eitan Suez
 */
public class ExceptionFrame extends CloseableJInternalFrame
{
   public ExceptionFrame(Throwable ex)
   {
      super("Exception", true, true, false, true);
      JPanel contentPane = (JPanel) getContentPane();
      
      if (ex instanceof InvocationTargetException)
      {
         InvocationTargetException indirect = (InvocationTargetException) ex;
         ex = indirect.getCause();
      }
      
      JLabel msgLabel = new JLabel("Exception:  " + ex.getMessage());
      Font font = msgLabel.getFont().deriveFont(Font.BOLD, 14.0f);
      msgLabel.setFont(font);
      contentPane.add(msgLabel, BorderLayout.NORTH);
      
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      JTextArea details = new JTextArea(sw.toString());
      details.setBackground(getBackground());
      details.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(details);
      scrollPane.setBorder(BorderFactory.createTitledBorder("Exception Details"));
      contentPane.add(scrollPane, BorderLayout.CENTER);
      
      pack();
   }

}
