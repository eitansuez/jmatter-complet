package com.u2d.view.swing.atom;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 6, 2008
 * Time: 2:53:50 PM
 */
public class MaxLength extends PlainDocument
{
   private int limit;

   MaxLength(int limit)
   {
      super();
      this.limit = limit;
   }

   public void insertString
         (int offset, String str, AttributeSet attr)
         throws BadLocationException
   {
      if (str == null) return;

      if ((getLength() + str.length()) <= limit)
      {
         super.insertString(offset, str, attr);
      }
   }
}
