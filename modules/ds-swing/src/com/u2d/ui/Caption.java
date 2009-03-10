/*
 * Created on Mar 4, 2004
 */
package com.u2d.ui;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * A caption is a JLabel that can span multiple lines.  The implementation
 * relies on the ability to use html for the label text (the <p> tag is 
 * used as a line break)
 * 
 * @author Eitan Suez
 */
public class Caption extends JLabel
{
   private int _breakPosition = 20;

   public Caption() {}
   
   public Caption(String text)
   {
      setText(text);
   }
   public Caption(String text, int breakPosition)
   {
      setBreakPosition(breakPosition);
      setText(text);
   }
   
   
   public void setBreakPosition(int index)
   {
      _breakPosition = index;
   }
    
   public void setText(String text)
   {
      String broken = multiLine(text);
      super.setText(broken);
   }
   
   
   private String multiLine(String text)
   {
      String original = text;
      
      boolean processed = false;
      if (text == null) text = "";
      String broken = "";
      while (text.length() > _breakPosition)
      {
         processed = true;
         String[] parts = breakLine(text);
         broken += parts[0];
         text = parts[1];
      }
      text = broken + text;
      
      if (getHorizontalAlignment() == SwingConstants.CENTER)
      {
         processed = true;
         text = center(text);
      }
      
      /*
       * avoid using html as much as possible because setmnemonic does
       * not work otherwise (!).
       */
      if (processed)
         return html(text);
      else
         return original;
   }
   
   private String html(String text)
   {
      if (text == null) return "<html></html>";
      if (text.toLowerCase().startsWith("<html>")) return text;
      return "<html>" + text + "</html>";
   }
   private String center(String text)
   {
      return "<center>" + text + "</center>";
   }
   
   private String[] breakLine(String text)
   {
      String leftPart = text.substring(0, _breakPosition);
      int index = leftPart.lastIndexOf(" ");
      String rest;
      
      if (index < 0)
      {
         // break the string:
         leftPart += "-";
         rest = "-" + text.substring(_breakPosition);
      }
      else
      {
         leftPart = leftPart.substring(0, index);
         rest = text.substring(index+1);
      }
      return new String[] {leftPart + "<p>", rest};
      
   }
   
}
