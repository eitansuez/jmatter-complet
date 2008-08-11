package com.u2d.view.swing;

import javax.swing.*;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.awt.GridLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 11, 2008
 * Time: 1:47:35 PM
 */
public class BuiltInIcons extends JPanel
{
   public BuiltInIcons()
   {
      UIDefaults uiDefaults = UIManager.getDefaults();
      Enumeration keys = uiDefaults.keys();

      List<JLabel> iconLbls = new ArrayList<JLabel>();
      int i=0;
      while (keys.hasMoreElements())
      {
         Object key = keys.nextElement();
         Object val = uiDefaults.get(key);
         if (val instanceof Icon)
         {
            i += 1;
            Icon icon = (Icon) val;
            String keyString = String.valueOf(key);
            JLabel lbl = new JLabel(keyString, icon, SwingConstants.CENTER);
//            JLabel lbl = new JLabel(keyString);
            lbl.setVerticalTextPosition(SwingConstants.TOP);
            iconLbls.add(lbl);
         }
         if (i>4) break;
      }
      
      int sqrt = (int) Math.sqrt(iconLbls.size());
      setLayout(new GridLayout(sqrt+1, sqrt));
      for (JLabel lbl : iconLbls)
      {
         System.out.println("adding label: "+lbl.getText());
         add(lbl);
      }
   }
   
   public static void main(String[] args)
   {
      JFrame f = new JFrame("Scroll Test");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setContentPane(new BuiltInIcons());
      f.setLocation(200,200);
      f.pack();
      f.setVisible(true);
   }
}
