/*
 * Created on March 3, 2004
 */
package com.u2d.view.swing;

import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.view.*;
import java.awt.*;
import javax.swing.*;
import java.beans.*;

/**
 * A tab body view is basically a FormView ornated with a TitleView at the top
 * 
 * @author Eitan Suez
 */
public class TabBodyView extends JPanel implements ComplexEView, Editor
{
   private ComplexEObject _ceo;
   private EView _main;
   private TitleView _titleView;
   
   public TabBodyView(ComplexEObject ceo)
   {
      _ceo = ceo;
      setLayout(new BorderLayout());
      _titleView = new TitleView(_ceo, this);
      add(_titleView, BorderLayout.PAGE_START);

      /*
       * discussion: an alternativeview is typically used only top-level.
       * in the context of an embedded view such as this one, we want just a formview..
       * instead of this logic, revise the contract to ask for mainview() and embeddedview()
       */
      _main = _ceo.getMainView();
      if (_main instanceof AlternateView)
      {
         _main.detach();
         _main = new FormView(_ceo);
      }


      add((JComponent) _main, BorderLayout.CENTER);
   }
   
	public void propertyChange(PropertyChangeEvent evt) {}
	public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return false; }

   public int transferValue() { return ((Editor) _main).transferValue(); }
   public int validateValue() { return ((Editor) _main).validateValue(); }

   public void setEditable(boolean editable) { ((Editor) _main).setEditable(editable); }
   public boolean isEditable() { return ((Editor) _main).isEditable(); }
   
   public void detach()
   {
      _titleView.detach();
      _main.detach();
   }

}
