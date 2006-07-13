package com.u2d.wizard.details;

import com.u2d.ui.DescriptionText;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 2, 2005
 * Time: 3:23:27 PM
 */
public class SimpleStepPanel extends JPanel
{
   DescriptionText _dText = new DescriptionText();

   public SimpleStepPanel(String msg)
   {
      _dText.setText(msg);

      Border border = BorderFactory.createLineBorder(Color.gray);
      Border margin = BorderFactory.createEmptyBorder(5, 0, 5, 1);
      Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);
      border = BorderFactory.createCompoundBorder(margin, border);
      border = BorderFactory.createCompoundBorder(border, padding);
      setBorder(border);

      setLayout(new BorderLayout());
      add(_dText, BorderLayout.CENTER);
   }
}
