package com.u2d.reporting;

import org.jfree.report.modules.gui.base.ExportTask;
import org.jfree.report.modules.gui.base.ReportProgressDialog;
import org.jfree.report.modules.gui.rtf.RTFExportTask;
import org.jfree.report.modules.gui.pdf.PDFExportTask;
import org.jfree.report.JFreeReport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 10, 2006
 * Time: 3:13:28 PM
 */
public enum ReportFormat
{
   PDF(PDFExportTask.class, ".pdf"), RTF(RTFExportTask.class, ".rtf");

   private Class _exportTaskType;
   private String _fileSuffix;

   ReportFormat(Class exportTaskType, String fileSuffix)
   {
      _exportTaskType = exportTaskType;
      _fileSuffix = fileSuffix;
   }

   public String fileSuffix() { return _fileSuffix; }

   public ExportTask exportTask(String fileName, JFreeReport report)
   {
      Constructor constr = null;
      try
      {
         constr = _exportTaskType.getConstructor(String.class, ReportProgressDialog.class, JFreeReport.class);
         return (ExportTask) constr.newInstance(fileName, null, report);
      }
      catch (NoSuchMethodException e)
      {
         e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
         e.printStackTrace();
      }
      catch (InvocationTargetException e)
      {
         e.printStackTrace();
      }
      catch (InstantiationException e)
      {
         e.printStackTrace();
      }
      throw new RuntimeException("Failed to create export task for report format "+this);
   }
   
   public String toString()
   {
      return "FileSuffix: "+_fileSuffix+"; "+"ExportClass: "+_exportTaskType.getName();
   }

}
