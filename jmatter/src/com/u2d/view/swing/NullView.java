package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Eitan Suez
 */
public class NullView extends JPanel implements ComplexEView
{
   ComplexEObject _ceo;
   public NullView(ComplexEObject ceo) { _ceo = ceo; }

   public void detach() { _ceo = null; } 
   public EObject getEObject() { return _ceo; }

   public void propertyChange(PropertyChangeEvent evt) { }
   public void stateChanged(javax.swing.event.ChangeEvent evt) { }
   
   Dimension dim = new Dimension(0, 0);
   public Dimension getPreferredSize() { return dim; }

   public boolean isMinimized() { return true; }
}
