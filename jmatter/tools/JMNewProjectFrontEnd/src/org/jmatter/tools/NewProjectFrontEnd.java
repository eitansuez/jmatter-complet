package org.jmatter.tools;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 16, 2006
 * Time: 12:26:51 PM
 */
public class NewProjectFrontEnd
{
   private JTextField projectNameFld;
   private JTextField projectBasedirFld;
   private JButton dirPickerBtn;
   private JRadioButton standaloneRadioButton;
   private JButton createProjectButton;
   private JPanel mainPanel;


   public NewProjectFrontEnd()
   {
      init();
   }

   private void init()
   {
      createProjectButton.setMnemonic('c');
      
      dirPickerBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showDialog(mainPanel, "Choose");
            if (result == JFileChooser.APPROVE_OPTION)
            {
               projectBasedirFld.setText(chooser.getSelectedFile().getAbsolutePath());
            }
         }
      });
      
      createProjectButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            String projectName = projectNameFld.getText().trim();
            String projectBaseDir = projectBasedirFld.getText().trim();
            
            boolean baseDirSpecified = 
                  !(projectBaseDir == null || projectBaseDir.length() == 0); 
            
            boolean standalone = standaloneRadioButton.isSelected();
            
            String cmd = String.format("ant new-project -Dnew.project.name=%s", projectName);
            if (baseDirSpecified)
               cmd += String.format(" -Dnew.project.basedir=%s", projectBaseDir);
            if (standalone)
               cmd += " -Dstandalone=true";
            
            final String finalCmd = cmd;
            
            new Thread()
            {
               public void run()
               {
                  try
                  {
                     Process p = Runtime.getRuntime().exec(finalCmd);
                     BufferedReader br = 
                           new BufferedReader(new InputStreamReader(p.getInputStream()));
                     String line;
                     while ( (line = br.readLine()) != null )
                     {
                        System.out.println(line);
                     }
                  }
                  catch (IOException e1)
                  {
                     e1.printStackTrace();
                  }
               }
            }.start();
         }
      });
   }


   public static void main(String[] args)
   {
      NewProjectFrontEnd form = new NewProjectFrontEnd();
      
      JFrame f = new JFrame("Create new JMatter project..");
      f.setContentPane(form.mainPanel);
      f.getRootPane().setDefaultButton(form.createProjectButton);
      
      f.setLocation(100,100);
      f.pack();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setVisible(true);
   }

}
