package org.jmatter.tools;

import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.BuildException;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

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
            final String projectName = projectNameFld.getText().trim();
            final String projectBaseDir = projectBasedirFld.getText().trim();
            final boolean baseDirSpecified = 
                  !(projectBaseDir == null || projectBaseDir.length() == 0); 
            final boolean standalone = standaloneRadioButton.isSelected();

            new Thread()
            {
               public void run()
               {
                  createNewProject(projectName, baseDirSpecified, projectBaseDir, standalone);
               }
            }.start();
         }
      });
   }
   
   private void createNewProject(String projectName, boolean baseDirSpecified, 
                                          String projectBaseDir, boolean standalone)
   {
      File buildFile = new File("build.xml");
      Project p = new Project();
      p.setUserProperty("ant.file", buildFile.getAbsolutePath());
      
      DefaultLogger consoleLogger = new DefaultLogger();
      consoleLogger.setErrorPrintStream(System.err);
      consoleLogger.setOutputPrintStream(System.out);
      consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
      p.addBuildListener(consoleLogger);

      try
      {
         p.fireBuildStarted();
         p.init();
         ProjectHelper helper = ProjectHelper.getProjectHelper();
         p.addReference("ant.projectHelper", helper);
         helper.parse(p, buildFile);
         
         p.setProperty("new.project.name", projectName);
         if (baseDirSpecified)
            p.setProperty("new.project.basedir", projectBaseDir);
         if (standalone)
            p.setProperty("standalone", "true");
         
         p.executeTarget("new-project");
         p.fireBuildFinished(null);
      }
      catch (BuildException e)
      {
         p.fireBuildFinished(e);
      }
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
