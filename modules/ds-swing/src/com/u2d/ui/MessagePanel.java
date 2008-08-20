package com.u2d.ui;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MessagePanel extends JPanel
{
   private TimerLabel _label;
   
   public MessagePanel()
   {
      setLayout(new FlowLayout(FlowLayout.LEADING));
      _label = new com.u2d.ui.TimerLabel(3000);
      add(_label);
      setBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
   }
   
   public void message(String text) { _label.setText(text); }
}
