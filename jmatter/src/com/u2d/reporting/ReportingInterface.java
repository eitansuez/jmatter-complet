package com.u2d.reporting;

import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.modules.parser.base.ReportGenerator;
import org.jfree.report.modules.gui.base.ExportTask;
import org.jfree.report.modules.gui.base.ExportTaskListener;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.ui.FloatDimension;
import org.jfree.base.log.LogConfiguration;
import org.jfree.util.Log;

import javax.swing.table.TableModel;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import com.lowagie.tools.Executable;
import com.u2d.utils.Launcher;  // TODO: replace with Mustang Desktop API

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 10, 2006
 * Time: 3:38:07 PM
 */
public class ReportingInterface
{
   public ReportingInterface()
   {
      LogConfiguration.setLogLevel("warn");
      Log.getInstance().init();
      JFreeReportBoot.getInstance().start();
   }
   
   public void displayReport(Reportable reportable)
   {
      try
      {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         java.net.URL layoutURL = loader.getResource(reportable.reportName());
         JFreeReport report = null;
         if (layoutURL == null)
         {
            report = new JFreeReport();
            report.setName(reportable.reportName());

            // it really sucks that jfree makes me do this..
            TableModel model = reportable.tableModel();
            TextFieldElementFactory factory = null;
            float start = 0;  int width = 200;
            for (int i=0; i<model.getColumnCount(); i++)
            {
               factory = new TextFieldElementFactory();
               factory.setAbsolutePosition(new Point2D.Float(start, 0));
               factory.setMinimumSize(new FloatDimension(width + start, 20));
               start += width;

               //factory.setHorizontalAlignment(ElementAlignment.LEFT);
               //factory.setVerticalAlignment(ElementAlignment.MIDDLE);
               //factory.setNullString("-");
               factory.setFieldname(model.getColumnName(i));
               report.getItemBand().addElement(factory.createElement());
               width = 100;
            }
         }
         else
         {
            ReportGenerator generator = ReportGenerator.getInstance();
            generator.setValidateDTD(false);
            report = generator.parseReport(layoutURL);
         }

         if (reportable.tableModel() != null &&
               reportable.tableModel().getRowCount() > 0)
         {
            report.setData(reportable.tableModel());
         }

         Iterator itr = reportable.properties().entrySet().iterator();
         Map.Entry entry;
         String key;
         while (itr.hasNext())
         {
            entry = (Map.Entry) itr.next();
            key = (String) entry.getKey();
            report.setProperty(key, entry.getValue());
            report.setPropertyMarked(key, true);
//            System.out.println("marked property "+key+" (value: "+entry.getValue()+")");
         }

         // instead, doing this:
         ReportFormat format = reportable.reportFormat();
         if (format == null) format = ReportFormat.PDF;

         final File reportFile = File.createTempFile("report", format.fileSuffix());
         reportFile.deleteOnExit();

         ExportTask task = format.exportTask(reportFile.getAbsolutePath(), report);

         task.addExportTaskListener(new ExportTaskListener()
            {
               public void taskDone(ExportTask task)
               {
                  try
                  {
                     if (Executable.isLinux())
                     {
                        Launcher.openFile(reportFile);
                     }
                     else
                     {
                        Executable.openDocument(reportFile);
                     }
                  }
                  catch (IOException ex)
                  {
                     System.err.println("IOException: "+ex.getMessage());
                     ex.printStackTrace();
                  }
               }
               public void taskAborted(ExportTask task) {}
               public void taskFailed(ExportTask task) {}
               public void taskWaiting(ExportTask task) {}
            });
         task.run();

      }
      catch (Exception ex)
      {
         System.err.println("Error loading or processing report: " + ex.getMessage());
         ex.printStackTrace();
      }
   }
   
}
