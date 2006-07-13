package com.u2d.reporting;

import com.lowagie.text.pdf.PdfWriter;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 31, 2006
 * Time: 4:58:07 PM
 */
public abstract class ITextPdfCallback implements ITextCallback
{
   protected PdfWriter writer;
   
   public void setWriter(PdfWriter writer)
   {
      this.writer = writer;
   }
}
