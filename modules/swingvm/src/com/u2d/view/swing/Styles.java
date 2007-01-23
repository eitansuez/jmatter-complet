package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 23, 2007
 * Time: 11:40:04 AM
 * 
 * Comments:  this is a first, small step towards trying to come up with an orthogonal
 *  way of styling jmatter applications.
 * 
 * One idea I will contemplate:  use css standards for serializing colors, fonts, and
 *   other style information (margins).  Then designate a properties file that
 *   can be used to specify styles.
 * 
 * Not sure yet how selectors can come into the picture.  Currently, simply going
 *    by a Java class name (such as the FieldCaption below) seems enough at the moment.
 * 
 * I am aware of other existing solutions, such as JAXX, but that would imply a 
 *    fair amount of work to integrate with JMatter.
 */
public class Styles
{
   static void initialize()
   {
      Font requiredFieldFont = UIManager.getFont("Label.font").deriveFont(Font.ITALIC);
      Color requiredFieldColor = Color.red;
      
      UIDefaults defaults = UIManager.getDefaults();
      defaults.put("FieldCaption.required.font", requiredFieldFont);
      defaults.put("FieldCaption.required.foreground", requiredFieldColor);
   }
}
