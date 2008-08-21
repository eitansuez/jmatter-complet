package org.jmatter.tools;

import org.apache.tools.ant.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import com.u2d.css4swing.CSSEngine;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 16, 2006
 * Time: 12:26:51 PM
 */
public class NewProjectFrontEnd implements BuildListener
{
   private JTextField projectNameFld;
   private JTextField projectBasedirFld;
   private JButton dirPickerBtn;
   private JRadioButton standaloneRadioButton;
   private JButton createProjectButton;
   private JPanel mainPnl;
   private JLabel statusLbl;
   private JLabel heading;
   private JPanel statusPnl;
   private JPanel bodyPnl;


   public NewProjectFrontEnd()
   {
      init();
   }

   private void init()
   {
      defineStyles();
      createProjectButton.setMnemonic('c');
      
      dirPickerBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showDialog(mainPnl, "Choose");
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
   
   private void defineStyles()
   {
      ComponentStyle.addClass(mainPnl, "mainPnl");
      ComponentStyle.addClass(bodyPnl, "bodyPnl");
      ComponentStyle.addClass(statusPnl, "statusPnl");
      ComponentStyle.addClass(heading, "heading");
   }
   
   private void createNewProject(String projectName, boolean baseDirSpecified, 
                                          String projectBaseDir, boolean standalone)
   {
      File buildFile = new File("build.xml");
      Project p = new Project();
      p.setUserProperty("ant.file", buildFile.getAbsolutePath());
      
      p.addBuildListener(this);

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
      CSSEngine.initialize();
      NewProjectFrontEnd form = new NewProjectFrontEnd();
      
      JFrame f = new JFrame("Create new JMatter project..");
//      f.setJMenuBar(new StyleMenuBar(f));
      f.setContentPane(form.mainPnl);
      f.getRootPane().setDefaultButton(form.createProjectButton);
      
      f.setLocation(100,100);
      f.pack();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setVisible(true);
   }

   
   // build listener interface..

   public void buildStarted(BuildEvent buildEvent)
   {
      statusLbl.setText("Build started..");
   }

   public void buildFinished(BuildEvent buildEvent)
   {
      statusLbl.setText("Build finished!");
   }

   public void targetStarted(BuildEvent buildEvent)
   {
      statusLbl.setText("Invoking target: "+buildEvent.getTarget().getName());
   }

   public void targetFinished(BuildEvent buildEvent)
   {
   }

   public void taskStarted(BuildEvent buildEvent)
   {
   }

   public void taskFinished(BuildEvent buildEvent)
   {
   }

   public void messageLogged(BuildEvent buildEvent)
   {
//      System.out.println(buildEvent.getMessage());
//      if (buildEvent.getException() != null)
//      {
//         buildEvent.getException().printStackTrace();
//      }
   }
}
