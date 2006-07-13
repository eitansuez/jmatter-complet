package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 1, 2005
 * Time: 5:00:06 PM
 *
 * @author Eitan Suez
 */
public class TitlePanel extends GradientPanel
{
   protected static Font TITLE_FONT;
   static
   {
      TITLE_FONT = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 16.0f);
   }

   protected JLabel _title = new JLabel();
   {
      _title.setFont(TITLE_FONT);
      _title.setOpaque(false);
   }

   public TitlePanel() { this(""); }
   public TitlePanel(String text) { this(Color.blue, false, text); }

   public TitlePanel(Color color, boolean transparent, String text)
   {
      super(color, transparent);
      setLayout(new BorderLayout());
      _title.setHorizontalTextPosition(JLabel.LEFT);
      add(_title, BorderLayout.CENTER);
      setText(text);
   }

   public void setText(String text) { _title.setText(text); }

   protected Insets _insets = new Insets(2, 5, 6, 8);
	public Insets getInsets() { return _insets; }

   // test:
   public static void main(String[] args)
   {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.getContentPane().add(new TitlePanel(Color.blue,  false, "Hello World"));
      f.setBounds(100,100,400,400);
      f.setVisible(true);

   }


}
