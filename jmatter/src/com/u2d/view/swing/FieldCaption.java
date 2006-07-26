/*
 * Created on Dec 14, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import javax.swing.*;
import com.u2d.element.Field;
import com.u2d.type.atom.BooleanEO;

/**
 * @author Eitan Suez
 */
public class FieldCaption extends com.u2d.ui.Caption
{
   private static Font BOLD_FONT;
   static
   {
      BOLD_FONT = UIManager.getFont("Label.font").deriveFont(Font.BOLD);
   }

   public FieldCaption(Field field, JComponent comp)
   {
      Class cls = field.getJavaClass();
      String appender = (cls.equals(BooleanEO.class)) ? "?" : ":";
      setText(field.label()+appender);
      setLabelFor(comp);
      
      if (field.hasMnemonic())
      {
         String text = field.label();
         char mnemonic = Character.toLowerCase(field.getMnemonic());
         int index = text.indexOf(mnemonic);
         if (index < 0)
         {
            mnemonic = Character.toUpperCase(field.getMnemonic());
            index = text.indexOf(mnemonic);
         }
         
         if (index >= 0)
         {
            setDisplayedMnemonic(mnemonic);
            setDisplayedMnemonicIndex(index);
         }
      }

      if (field.required())
      {
         setFont(BOLD_FONT);
         setForeground(Color.BLUE);
      }
   }

}
