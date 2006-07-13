package com.u2d.wizard.ui;

import com.u2d.wizard.abstractions.Step;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 26, 2005
 * Time: 4:09:05 PM
 */
public class NavPane extends JPanel
{
   JButton _previousBtn, _nextBtn, _finishBtn, _cancelBtn;
   {
      _previousBtn = new JButton("Previous", NAV_LEFT);
      _previousBtn.setHorizontalTextPosition(AbstractButton.TRAILING);
      _previousBtn.setMnemonic('p');

      _nextBtn = new JButton("Next", NAV_RIGHT);
      _nextBtn.setHorizontalTextPosition(AbstractButton.LEADING);
      _nextBtn.setMnemonic('n');

      _finishBtn = new JButton("Finish");
      _finishBtn.setMnemonic('f');

      _cancelBtn = new JButton("Cancel");
      _cancelBtn.setMnemonic('c');
   }

   public NavPane()
   {
      ButtonBarBuilder builder = new ButtonBarBuilder(this);
      builder.addGlue();

      JButton[] buttons = {_previousBtn, _nextBtn, _finishBtn, _cancelBtn};
      builder.addGriddedButtons(buttons);
   }

   public void setNextAction(ActionListener l) { _nextBtn.addActionListener(l); }
   public void setPreviousAction(ActionListener l) { _previousBtn.addActionListener(l); }
   public void setCancelAction(ActionListener l) { _cancelBtn.addActionListener(l); }
   public void setFinishAction(ActionListener l) { _finishBtn.addActionListener(l); }

   public void updateStep(Step step)
   {
      _nextBtn.setEnabled(step.hasNextStep());
      _previousBtn.setEnabled(step.hasPreviousStep());
      _cancelBtn.setEnabled(step.hasNextStep());
      _finishBtn.setEnabled(!step.hasNextStep());
   }

   public static ImageIcon NAV_LEFT, NAV_RIGHT;
   static
   {
      ClassLoader loader = NavPane.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/navigate_left.png");
      NAV_LEFT = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/navigate_right.png");
      NAV_RIGHT = new ImageIcon(imgURL);
   }

   public void addNotify()
   {
      super.addNotify();
      getRootPane().setDefaultButton(_nextBtn);
   }

}
