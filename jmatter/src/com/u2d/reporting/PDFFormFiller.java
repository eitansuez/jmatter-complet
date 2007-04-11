package com.u2d.reporting;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.u2d.utils.Launcher;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 10, 2007
 * Time: 7:16:49 PM
 */
public class PDFFormFiller
{
   private InputStream resourceStream;
   private List<Set<TextWithCoords>> pages = new ArrayList<Set<TextWithCoords>>();
   private Set<TextWithCoords> currentSet;

   public PDFFormFiller()
   {
      addPage(); // min 1 pages
   }

   public PDFFormFiller(InputStream resource)
   {
      this();
      setResourceStream(resource);
   }

   public void setResourceStream(InputStream resource)
   {
      resourceStream = resource;
   }
   public void addPage()
   {
      pages.add(new HashSet<TextWithCoords>());
      setPage(pages.size());
   }
   public void setPage(int pageNumber)
   {
      currentSet = pages.get(pageNumber-1);
   }
   
   public void addText(String text, int xpos, int ypos)
   {
      currentSet.add(new TextWithCoords(text, xpos, ypos));
   }
   
   public void fill() throws IOException, DocumentException
   {
      PdfReader reader = new PdfReader(resourceStream);

      File reportFile = File.createTempFile("report", ".pdf");
      reportFile.deleteOnExit();
      
      PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(reportFile));
      BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      
      for (int i=0; i< pages.size(); i++)
      {
         PdfContentByte cb = stamp.getOverContent(i+1);
         cb.setFontAndSize(bf, 10);
         cb.beginText();
         
         for (TextWithCoords twc : pages.get(i))
         {
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, twc.text(), twc.xpos(), twc.ypos(), 0);
         }

         cb.endText();
      }
      
      stamp.close();
      
      Launcher.openFile(reportFile);
   }
   
}
