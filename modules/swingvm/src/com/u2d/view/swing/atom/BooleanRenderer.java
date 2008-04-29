package com.u2d.view.swing.atom;

import javax.swing.JLabel;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.BooleanEO;

/**
 * Date: Jun 8, 2005
 * Time: 1:46:35 PM
 *
 * @author Eitan Suez
 */
public class BooleanRenderer extends JLabel implements AtomicRenderer
{
   private String _yes, _no;
   public BooleanRenderer()
   {
      this(ComplexType.localeLookupStatic("yes"), ComplexType.localeLookupStatic("no"));
   }
   public BooleanRenderer(String yes, String no)
   {
      _yes = yes;
      _no = no;
   }
   public void render(AtomicEObject value)
   {
      BooleanEO eo = (BooleanEO) value;
      setText((eo.booleanValue()) ? _yes : _no);
   }

   public void passivate() { }
}
