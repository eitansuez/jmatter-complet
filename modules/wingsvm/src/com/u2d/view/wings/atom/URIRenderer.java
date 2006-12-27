package com.u2d.view.wings.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEObject;
import org.wings.SLabel;
import org.wings.SAnchor;

/**
 * Date: Jun 8, 2005
 * Time: 2:47:28 PM
 *
 * A Hyperlink View
 *
 * @author Eitan Suez
 */
public class URIRenderer extends SAnchor implements AtomicRenderer
{
   SLabel _label = new SLabel();
   
   public URIRenderer()
   {
      add(_label);
   }

   public void render(AtomicEObject value)
   {
      String text = value.toString();
      setURL(text);
      _label.setText(text);
   }

   public void passivate() { }
}
