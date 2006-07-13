package com.u2d.xml;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.jibx.runtime.JiBXException;
import com.u2d.element.CommandInfo;
import com.u2d.model.ComplexEObject;

public class XMLExport
{
   private static File START_PATH = new File(System.getProperty("user.home"));

   public static void export(CommandInfo cmdInfo, ComplexEObject ceo)
     throws IOException, JiBXException
   {
      JFileChooser chooser = new JFileChooser(START_PATH);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      
      String fileName = ceo.title() + ".xml";
      String targetFileName = START_PATH + File.separator + fileName;
      
      chooser.setSelectedFile(new File(targetFileName));
      int result = chooser.showSaveDialog((Component) cmdInfo.getSource());
      if (result != JFileChooser.APPROVE_OPTION)
         return;

      File targetFile = chooser.getSelectedFile();
      START_PATH = targetFile.getParentFile();

      new JibxBoiler(ceo.getClass()).marshal(ceo, targetFile.getCanonicalPath());
   }
}
