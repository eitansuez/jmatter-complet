package com.u2d.view.wings;

import com.u2d.view.CompositeView;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.wings.atom.CommandsButtonView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AtomicEObject;
import com.u2d.model.EObject;
import com.u2d.element.Field;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import org.wings.SPanel;
import org.wings.SBorderLayout;
import org.wings.SForm;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 24, 2006
 * Time: 3:26:11 PM
 */
public class TopLevelFormView extends SForm
      implements CompositeView, ComplexEView, Editor
{
   private ComplexEObject _ceo;

   private transient CommandsButtonView _cmdsView;
   private FormView _formView;
   private StatusPanel _statusPanel;

   public TopLevelFormView(ComplexEObject ceo)
   {
      _ceo = ceo;

      setLayout(new SBorderLayout());

      _formView = new FormView(ceo);
      add(_formView, SBorderLayout.CENTER);

      _cmdsView = new CommandsButtonView();
      _cmdsView.bind(_ceo, this, SBorderLayout.EAST, this);

      add(statusPanel(), SBorderLayout.SOUTH);
   }

   private SPanel statusPanel()
   {
      _statusPanel = new StatusPanel();
      if (!(_ceo.isMeta()))
         _statusPanel.addEO(_ceo.getCreatedOn());

      Field statusField = _ceo.field("status");
      if (statusField != null)
      {
         _statusPanel.addEO((AtomicEObject) statusField.get(_ceo));
      }
      return _statusPanel;
   }

   public void detach()
   {
      _formView.detach();
      _cmdsView.detach();
      _statusPanel.detach();
   }

   public int transferValue() { return _formView.transferValue(); }
   public void setEditable(boolean editable) { _formView.setEditable(editable); }
   public boolean isEditable() { return _formView.isEditable(); }

   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return false; }
   public EView getInnerView() { return _formView; }
}
