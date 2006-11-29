/*
 * Created on Nov 20, 2003
 */
package com.u2d.type.composite;

import com.u2d.element.CommandInfo;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.app.User;
import com.u2d.reflection.Cmd;
import java.util.*;
import java.awt.*;
import java.awt.print.*;

/**
 * @author Eitan Suez
 */
public class Note extends AbstractComplexEObject implements Printable
{
	private final StringEO _subject = new StringEO();
   private final TextEO _text = new TextEO();
	private final DateTime _date = new DateTime(new Date());
   private User _author;
   
   public static String[] fieldOrder = {"subject", "date", "author", "text"};
   public static String[] readOnly = {"date", "author"};

   public Note() {}

   public void initialize() { _author = currentUser(); }

   public StringEO getSubject() { return _subject; }
	public DateTime getDate() { return _date; }
	public TextEO getText() { return _text; }
   
   public User getAuthor() { return _author; }
   public void setAuthor(User user)
   {
      User oldAuthor = _author;
      _author = user;
      firePropertyChange("author", oldAuthor, _author);
   }
	
   public Title title()
   {
      if (_subject.isEmpty())
         return new Title("[blank]");
      return _subject.title().appendParens(_date);
   }
   
   
   @Cmd
   public void Print(CommandInfo cmdInfo) throws PrinterException
   {
      PrinterJob job = PrinterJob.getPrinterJob();
      job.setPrintable(Note.this);
      if (!job.printDialog())
         return;
      
      job.print();
   }
   
   public int print(Graphics g, PageFormat pageFormat, int pageIndex)
   {
      FontMetrics fm = g.getFontMetrics();
      g.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
      int x = 0;
      int lineHeight = fm.getHeight();
      int pageWidth = (int) pageFormat.getImageableWidth();
      int pageHeight = (int) pageFormat.getImageableHeight();
      
      java.util.List allLines = new ArrayList();
      
      // 1. prepare header lines:
      allLines.add("Date: "+_date);
      allLines.add("By: "+_author);
      allLines.add("Subject: "+_subject);
      
      for (int i=0; i<2; i++)
         allLines.add("");
      
      if (pageIndex == 0)
      {
         Graphics2D g2 = (Graphics2D) g;
         g2.setStroke(new BasicStroke(3));
         int lineYPos = (int) (fm.getAscent() + 2.5 * lineHeight);
         g.drawLine(x, lineYPos, (int) pageWidth, lineYPos);
      }
      
      // 2. prepare body lines:
      allLines.addAll(formatText(_text, fm, pageWidth));
      
      int linesPerPage = Math.max(pageHeight/lineHeight, 1);
      int numPages = (int) Math.ceil((double) allLines.size() / (double) linesPerPage);
      
      if (pageIndex >= numPages)
         return Printable.NO_SUCH_PAGE;
      
      int endIndex = Math.min(linesPerPage*(pageIndex+1), allLines.size());
      java.util.List pageLines = allLines.subList(linesPerPage*pageIndex, endIndex);
      
      // 3. print the lines
      int y = fm.getAscent();
      for (int i=0; i<pageLines.size(); i++)
      {
         g.drawString((String) pageLines.get(i), x, y);
         y += lineHeight;
      }
      
      return Printable.PAGE_EXISTS;
   }
   
   private java.util.List formatText(TextEO text, FontMetrics fm, double pageWidth)
   {
      String data = text.stringValue();
      
      String delims = "\n\r";
      StringTokenizer tokenizer = new StringTokenizer(data, delims, true);
      java.util.List lines = new ArrayList(10);
      
      // issue:  two delimiters back-to-back are treated as a single delimiter so there's
      // no way to detect a null item as in this example using delimiter / : one//two/three
      // that's what the initial logic is for: tell stringtokenizer to include delimiters
      // as tokens and if multiple detected, just skip first
      
      boolean initial = true;
      while (tokenizer.hasMoreTokens())
      {
         String paragraph = tokenizer.nextToken();
         
         if ((delims.indexOf(paragraph) > -1) && initial)
         {
            initial = false;
            continue;
         }
         
         PrintIterator itr = new PrintIterator(paragraph, fm, pageWidth);
         while (itr.hasNext())
         {
           String line = itr.next();
           lines.add(line);
         }
         initial = true;
      }
      return lines;
   }
   
   class PrintIterator
   {
      String _paragraph;
      FontMetrics _fm;
      double _pageWidth;
      
      int _lineCharLength;
      String _remainder;
      boolean _done = false;
      
      PrintIterator(String paragraph, FontMetrics fm, double pageWidth)
      {
         _paragraph = paragraph;
         _fm = fm;
         _pageWidth = pageWidth;
         
         int numLinesRough = (int) (fm.stringWidth(paragraph) / pageWidth) + 1;
         _lineCharLength = paragraph.length() / numLinesRough;
         _remainder = new String(_paragraph);
      }
      
      public boolean hasNext()
      {
         return !_done;
      }
      
      public String next()
      {
         int amount = Math.min(_lineCharLength, _remainder.length());
         _done = (amount == _remainder.length());
         
         String chunk = _remainder.substring(0, amount);
         
         int lastSpace = chunk.lastIndexOf(" ");
         if (!_done && lastSpace >= 0)
            amount = lastSpace + 1;
         
         String line = _remainder.substring(0, amount);
         if (!_done)
            _remainder = _remainder.substring(amount);
         
         return line;
      }
      
      
   }
   
}
