package com.u2d.reporting;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 10, 2007
 * Time: 5:06:41 PM
 */
public class TextWithCoords
{
   private String text = "";
   private int xpos, ypos;
   
   public TextWithCoords(String text, int xpos, int ypos)
   {
      if (text == null) throw new IllegalArgumentException("invalid text value: "+text);
      this.text = text;
      this.xpos = xpos; this.ypos = ypos;
   }
   
   public String text() { return text; }
   public int xpos() { return xpos; }
   public int ypos() { return ypos; }
   
}
