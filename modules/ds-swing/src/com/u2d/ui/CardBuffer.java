package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Date: May 17, 2005
 * Time: 3:23:21 PM
 *
 * @author Eitan Suez
 */
public class CardBuffer extends JPanel
{
   private CardLayout _cardLayout = new CardLayout();
   private String _currentName;
   private Component one, two;

   public CardBuffer(Component comp)
   {
      setLayout(_cardLayout);
      setOpaque(false);

      add(comp, "one");
      one = comp;
      _currentName = "one";
      _cardLayout.show(this, "one");
   }

   public Component switchIn(Component comp)
   {
      if ("one".equals(_currentName))
      {
         add(comp, "two");
         two = comp;
         _currentName = "two";
         _cardLayout.show(this, "two");
         remove(one);
         return one;
      }
      else
      {
         add(comp, "one");
         one = comp;
         _currentName = "one";
         _cardLayout.show(this, "one");
         remove(two);
         return two;
      }
   }
   
   public Component getCurrentItem()
   {
      if ("one".equals(_currentName))
         return one;
      else
         return two;
   }

   public Dimension getPreferredSize()
   {
      Component[] children = getComponents();
      for (int i=0; i<children.length; i++)
      {
         if (children[i].isVisible())
         {
            return children[i].getPreferredSize();
         }
      }
      return super.getPreferredSize();
   }
}
