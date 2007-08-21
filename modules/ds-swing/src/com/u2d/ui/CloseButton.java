package com.u2d.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 21, 2007
 * Time: 11:12:03 AM
 */
public class CloseButton extends IconButton
{
   static BufferedImage closeImg;
   static
   {
      try
      {
         ClassLoader loader = CloseButton.class.getClassLoader();
         URL url = loader.getResource("com/u2d/ui/x_close.png");
         closeImg = ImageIO.read(url);
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }
   
   public CloseButton()
   {
      super(new ImageIcon(closeImg));
   }
}
