/*
 * Created on Apr 2, 2005
 */
package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class CardPanel extends JPanel
{
   private CardLayout _cardLayout = new CardLayout();
   private String _currentCardName = null;

   public CardPanel()
   {
      setLayout(_cardLayout);
      setOpaque(false);
   }

   public void add(Component component, String name)
   {
      super.add(component, name);
   }

   public Component get(String name)
   {
      Component[] children = getComponents();
      for (int i=0; i<children.length; i++)
      {
         if (children[i].isVisible())
         {
            return children[i];
         }
      }
      return null;
   }

   public void show(String name)
   {
      _cardLayout.show(this, name);
      _currentCardName = name;
   }

   public void addShown(Component component, String name)
   {
      add(component, name);
      show(name);
   }

   public String getCurrentCardName() { return _currentCardName; }

   public Dimension getPreferredSize()
   {
      Component currentCard = get(_currentCardName);
      if (currentCard == null)
         return super.getPreferredSize();
      else
         return currentCard.getPreferredSize();
   }

}
