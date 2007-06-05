package com.u2d.wizard.ui;

import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.ui.GradientPanel;
import com.u2d.ui.DescriptionText;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 26, 2005
 * Time: 3:45:03 PM
 */
public class TitlePane extends GradientPanel
{
   public static final Color BGCOLOR = new Color(0x4169aa);
   protected static Font TITLE_FONT;
   static
   {
      TITLE_FONT = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 16.0f);
   }

   JLabel _titleLbl = new JLabel();
   DescriptionText _descriptionText = new DescriptionText();

   {
      _titleLbl.setFont(TITLE_FONT);
      _titleLbl.setOpaque(false);
      _titleLbl.setForeground(Color.white);

      _descriptionText.setForeground(Color.DARK_GRAY);
      _descriptionText.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      Font italic = _descriptionText.getFont().deriveFont(Font.ITALIC);
      _descriptionText.setFont(italic);
   }

   public TitlePane()
   {
      super(BGCOLOR, false);

      setLayout(new BorderLayout());
      add(_titleLbl, BorderLayout.NORTH);
      add(_descriptionText, BorderLayout.CENTER);

      Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
      Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
      Border border = BorderFactory.createCompoundBorder(etched, padding);
      setBorder(border);
   }

   public void updateStep(Step step)
   {
      String title = ((CompositeStep) step).numberedTitle();
      _titleLbl.setText(title);
      _descriptionText.setText(step.description());
   }
}

