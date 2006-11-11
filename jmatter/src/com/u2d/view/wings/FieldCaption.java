package com.u2d.view.wings;

import com.u2d.element.Field;
import com.u2d.type.atom.BooleanEO;
import org.wings.SFont;
import org.wings.SComponent;
import org.wings.SLabel;
import java.awt.Color;

/**
 * @author Eitan Suez
 */
public class FieldCaption extends SLabel
{
   private static SFont BOLD_FONT;
   static
   {
      BOLD_FONT = new SFont();
      BOLD_FONT.setSize(SFont.BOLD);
   }

   public FieldCaption(Field field, SComponent comp)
   {
      Class cls = field.getJavaClass();
      String appender = (cls.equals(BooleanEO.class)) ? "?" : ":";
      setText(field.label()+appender);
//      setLabelFor(comp);

      if (field.hasMnemonic())
      {
         String text = field.label();
         char mnemonic = Character.toLowerCase(field.mnemonic());
         int index = text.indexOf(mnemonic);
         if (index < 0)
         {
            mnemonic = Character.toUpperCase(field.mnemonic());
            index = text.indexOf(mnemonic);
         }

         if (index >= 0)
         {
//            setDisplayedMnemonic(mnemonic);
//            setDisplayedMnemonicIndex(index);
         }
      }

      if (field.required())
      {
         setFont(com.u2d.view.wings.FieldCaption.BOLD_FONT);
         setForeground(Color.BLUE);
      }
   }

}
