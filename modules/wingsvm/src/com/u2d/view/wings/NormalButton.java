package com.u2d.view.wings;

import org.wings.SButton;

/**
 * @author Eitan Suez
 */
public class NormalButton extends SButton
{
   public NormalButton()
   {
      super();
      setCharacteristics();
   }
   public NormalButton(String caption)
   {
      super(caption);
      setCharacteristics();
   }
   public NormalButton(javax.swing.Action action)
   {
      super(action);
      setCharacteristics();
   }

   private void setCharacteristics()
   {
      // possibly setup margins.. [tbd]
   }
}
