package com.u2d.reporting;

import com.lowagie.text.*;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 4, 2006
 * Time: 4:58:27 PM
 */
public class ITextUtils
{
   public static Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
   public static Font boldFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
   public static Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

   public static com.lowagie.text.List list()
   {
      com.lowagie.text.List list = new com.lowagie.text.List(false, 8);
      list.setListSymbol("\u2022");
      return list;
   }
   public static ListItem li(Paragraph p) { return new ListItem(p); }

   public static PdfPCell cell(Paragraph p, Rectangle border)
   {
      PdfPCell cell = new PdfPCell(p);
      cell.cloneNonPositionParameters(border);
      return cell;
   }
   public static PdfPCell cell(Paragraph p, int halign)
   {
      PdfPCell cell = new PdfPCell(p);
      cell.setHorizontalAlignment(halign);
      return cell;
   }
   public static PdfPCell cell(Paragraph p, Rectangle border, int halign)
   {
      PdfPCell cell = cell(p, border);
      cell.setHorizontalAlignment(halign);
      return cell;
   }

   public static Rectangle border(float thickness, int sides)
   {
      Rectangle border = new Rectangle(0, 0);
      border.setBorderColor(Color.BLACK);
      border.setBorderWidth(thickness);
      border.setBorder(sides);
      return border;
   }

   public static Paragraph p(String text)
   {
      return new Paragraph(text, font);
   }
   public static Paragraph h(String text)
   {
      return new Paragraph(text, boldFont);
   }
   public static Paragraph f(String text)
   {
      return new Paragraph(text, smallFont);
   }

   public static void addSpace(Document doc, int height)
         throws DocumentException
   {
      Paragraph p = new Paragraph();
      p.setSpacingBefore(height);
      doc.add(p);
   }
   public static void addSpace(Document doc)
         throws DocumentException
   {
      addSpace(doc, 12);
   }
}
