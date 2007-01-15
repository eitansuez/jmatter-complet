package com.u2d.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 11, 2007
 * Time: 1:59:40 PM
 */
public class GridListTest
      extends JPanel
{
   String[] data = {"one", "two", "three", "four", "five", "six", "seven"};
   private static Border EMPTYBORDER = BorderFactory.createEmptyBorder(1,1,1,1);

   public GridListTest()
   {
      setLayout(new BorderLayout());

      GridList list = new GridList(data);
      ListCellRenderer renderer = new ListCellRenderer()
      {
         JLabel label = new JLabel();
         {
            label.setOpaque(true);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setHorizontalAlignment(JLabel.CENTER);
            
            URL url = null;
            try
            {
               url = new URL("file:///home/eitan/work/jmatter-complet/modules/ds-swing/test/notebook.png");
               Icon icon = new ImageIcon(url);
               label.setIcon(icon);
            }
            catch (MalformedURLException e)
            {
               e.printStackTrace();
            }
         }
         
         public Component getListCellRendererComponent(JList list, Object value, int index, 
                                                       boolean isSelected, boolean cellHasFocus)
         {
            label.setText(value.toString());
      
            label.setBackground( isSelected ? 
                  UIManager.getColor("List.selectionBackground") : list.getBackground() );

            label.setForeground( isSelected ? 
                  UIManager.getColor("List.selectionForeground") : list.getForeground() );
      
            label.setBorder( cellHasFocus ? 
                  UIManager.getBorder("List.focusCellHighlightBorder") : EMPTYBORDER );

            return label;
         }
      };
      list.setCellRenderer(renderer);
      list.setSize(200,200);


      JScrollPane sp = new JScrollPane(list);
      add(sp, BorderLayout.CENTER);
   }
   public static void main(String[] args)
   {
      JFrame f = new JFrame("List Layout Test");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setContentPane(new GridListTest());
      f.pack();
      f.setLocation(200,200);
      f.setVisible(true);
   }
}

