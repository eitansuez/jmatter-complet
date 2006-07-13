package com.u2d.reporting;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.tools.Executable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 11, 2006
 * Time: 11:50:28 AM
 */
public class ITextTemplate
{
   public void produceRTF(ITextCallback callback) throws IOException, DocumentException
   {
      Document doc = new Document(PageSize.LETTER);
      File reportFile = File.createTempFile("report", ".rtf");
      reportFile.deleteOnExit();
      RtfWriter2.getInstance(doc, new FileOutputStream(reportFile));
      try
      {
         doc.open();
         callback.doInIText(doc);
      }
      finally
      {
         doc.close();
      }

      // java 1.6:
//      Desktop.getDesktop().open(reportFile);
      launchRtf(reportFile);
   }
   
   public void producePDF(ITextCallback callback) throws IOException, DocumentException
   {
      Document doc = new Document(PageSize.LETTER);
      File reportFile = File.createTempFile("report", ".pdf");
      reportFile.deleteOnExit();
      PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(reportFile));
      if (callback instanceof ITextPdfCallback)
      {
         ((ITextPdfCallback) callback).setWriter(writer);
         // in case callback needs writer for doing directContent stuff..
      }
      
      try
      {
         doc.open();
         callback.doInIText(doc);
      }
      finally
      {
         doc.close();
      }
      if (Executable.isLinux())
      {
         Executable.acroread = "/usr/bin/acroread";
      }
      Executable.openDocument(reportFile);
   }

   /*
    * Note: Java 6 will fix all this nonsense with auto registry-lookup
    * of application registered to handle file type, in a platform-neutral
    * way.
    */
   private void launchRtf(File reportFile)
   {
      String filePath = reportFile.getAbsolutePath();
      String[] params = new String[2];
      String[] paramsFallback = new String[2];
      
      // since itexttools is in my classpath, i might as well make use of it..
      if (Executable.isWindows() && Executable.isWindows9X())
      {
         params = new String[] {"command.com /C start winword", filePath};
         paramsFallback = new String[] {"command.com /C start wordpad", filePath};
      }
      else if (Executable.isWindows())
      {
         params = new String[] {"cmd /c start winword", filePath};
         paramsFallback = new String[] {"cmd /c start wordpad", filePath};
      }
      if (Executable.isLinux())
      {
         params = new String[] {"/usr/bin/ooffice2", "-Writer", filePath};
         paramsFallback = new String[] {"/usr/bin/ooffice", "-Writer", filePath};
      }
      else if (Executable.isMac())
      {
         params = new String[] {"/usr/bin/open", filePath};
      }
      
      try
      {
         exec2(params);
      }
      catch (IOException ex)
      {
         if (!Executable.isMac())
         {
            try
            {
               exec2(paramsFallback);
            }
            catch (IOException ex2)
            {
               ex2.printStackTrace();
            }
         }
      }
   }

   private void exec(String[] params)
         throws IOException
   {
      new ProcessBuilder(params).start();
   }
   private void exec2(String[] params)
         throws IOException
   {
      String cmd = concatenate(params);
      Runtime.getRuntime().exec(cmd);
   }
   private String concatenate(String[] params)
   {
      String result = "";
      int lastIdx = params.length - 1;
      for (int i=0; i<lastIdx; i++)
      {
         result += params[i] + " ";
      }
      result += params[lastIdx];
      return result;
   }


}
