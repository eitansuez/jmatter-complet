package com.u2d.view.swing;

import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 26, 2007
 * Time: 11:58:31 AM
 */
public class TextWithMnemonic
{
   private String text;
   private char mnemonic;
   private boolean hasMnemonic;

   public TextWithMnemonic resolve(String key)
   {
      text = ComplexType.localeLookupStatic(key);
      hasMnemonic = text.contains("&");
      if (hasMnemonic)
      {
         int index = text.indexOf('&');
         mnemonic = text.charAt(index + 1);
         text = text.substring(0, index) + text.substring(index + 1);
      }
      return this;
   }
   
   public String text() { return text; }
   public char mnemonic() { return mnemonic; }
   public boolean hasMnemonic() { return hasMnemonic; }

   public static TextWithMnemonic lookup(String key)
   {
      return new TextWithMnemonic().resolve(key);
   }
}
