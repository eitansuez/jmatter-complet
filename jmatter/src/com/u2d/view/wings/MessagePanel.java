package com.u2d.view.wings;

import org.wings.SPanel;
import org.wings.SFlowLayout;
import org.wings.border.SEtchedBorder;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 22, 2006
 * Time: 11:46:14 PM
 */
public class MessagePanel extends SPanel
{
   private TimerLabel _label;

   public MessagePanel()
   {
      setLayout(new SFlowLayout(SFlowLayout.LEFT));
      _label = new TimerLabel(3000);
      add(_label);
      setBorder(new SEtchedBorder(SEtchedBorder.LOWERED));
   }

   public void message(String text) { _label.setText(text); }
}
   

