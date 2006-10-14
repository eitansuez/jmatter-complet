package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import java.beans.PropertyChangeEvent;
import org.wings.*;

/**
 * A tab body view is basically a FormView ornated with a TitleView at the top
 * 
 * @author Eitan Suez
 */
public class TabBodyView
      extends SPanel
      implements ComplexEView, Editor
{
   private ComplexEObject _ceo;
   private EView _main;
   private TitleView _titleView;
   
   public TabBodyView(ComplexEObject ceo)
   {
      _ceo = ceo;
      setLayout(new SBorderLayout());
      _titleView = new TitleView(_ceo, this);
      add(_titleView, SBorderLayout.NORTH);
//      _main = _ceo.getMainView();
      _main = new FormView(_ceo);
      // TODO: put this back in after implement alternate views for wings..
//      if (_main instanceof AlternateView)
//      {
//         _main = new FormView(_ceo);  //getmainview constructs a formview with toplevelcontext=true
//           // which is bad, as these formviews are not toplevelcontext by definition
//           // yet calling getMainView() is essential for types that customize their views.
//           // TODO: come up with a better solution than this short-term fix.
//      }
      add((SComponent) _main, SBorderLayout.CENTER);
   }
   
	public void propertyChange(PropertyChangeEvent evt) {}
	public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return false; }

   public int transferValue() { return ((Editor) _main).transferValue(); }
   public void setEditable(boolean editable) { ((Editor) _main).setEditable(editable); }
   public boolean isEditable() { return ((Editor) _main).isEditable(); }
   
   public void detach()
   {
      _titleView.detach();
      _main.detach();
   }

}
