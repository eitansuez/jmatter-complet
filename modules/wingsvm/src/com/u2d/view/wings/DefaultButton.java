package com.u2d.view.wings;

import org.wings.SFont;
import org.wings.session.SessionManager;

/**
 * @author Eitan Suez
 */
public class DefaultButton extends NormalButton
{
   private static SFont BOLD = new SFont();
   static
   {
      BOLD.setStyle(SFont.BOLD);
   }

   public DefaultButton()
   {
      super();
      setCharacteristics();
   }
   public DefaultButton(String caption)
   {
      super(caption);
      setCharacteristics();
   }
   public DefaultButton(javax.swing.Action action)
   {
      super(action);
      setCharacteristics();
   }

   private void setCharacteristics()
   {
      setFont(BOLD);
   }
   
}
