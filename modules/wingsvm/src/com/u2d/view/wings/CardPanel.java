package com.u2d.view.wings;

import org.wings.SPanel;
import org.wings.SCardLayout;
import org.wings.SComponent;
import org.wings.SDimension;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 17, 2006
 * Time: 3:26:51 PM
 */

/**
 * @author Eitan Suez
 */
public class CardPanel extends SPanel
{
   private SCardLayout _cardLayout = new SCardLayout();
   private String _currentCardName = null;

   public CardPanel()
   {
      setLayout(_cardLayout);
   }

   public void add(SComponent component, String name)
   {
      super.add(component, name);
   }

   public SComponent get(String name)
   {
      SComponent[] children = getComponents();
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

   public void addShown(SComponent component, String name)
   {
      add(component, name);
      show(name);
   }

   public String getCurrentCardName() { return _currentCardName; }

   public SDimension getPreferredSize()
   {
      SComponent currentCard = get(_currentCardName);
      if (currentCard == null)
         return super.getPreferredSize();
      else
         return currentCard.getPreferredSize();
   }

}
