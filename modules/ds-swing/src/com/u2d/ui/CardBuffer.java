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
         if (comp == one) return comp;
         add(comp, "two");
         two = comp;
         _currentName = "two";
         _cardLayout.show(this, "two");
         remove(one);
         return one;
      }
      else
      {
         if (comp == two) return comp;
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
   
   public Component[] getItems()
   {
      return new Component[] { one, two };
   }

   public Dimension getPreferredSize()
   {
      Component[] children = getComponents();
      for (Component child : children)
      {
         if (child.isVisible())
         {
            return child.getPreferredSize();
         }
      }
      return super.getPreferredSize();
   }
}
