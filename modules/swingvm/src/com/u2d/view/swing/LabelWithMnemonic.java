package com.u2d.view.swing;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 23, 2008
 * Time: 9:57:13 PM
 */
public class LabelWithMnemonic extends JLabel
{
   TextWithMnemonic twm;

   public LabelWithMnemonic(String key)
   {
      twm = TextWithMnemonic.lookup(key);
      setText(twm.text());
      if (twm.hasMnemonic())
      {
         setDisplayedMnemonic(twm.mnemonic());
      }
   }
}
