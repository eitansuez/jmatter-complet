package com.u2d.view.swing;

import com.u2d.view.CompositeView;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.list.CommandsButtonView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.AtomicEObject;
import com.u2d.model.Editor;
import com.u2d.element.Field;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 24, 2006
 * Time: 3:26:11 PM
 */
public class TopLevelFormView extends JPanel
      implements CompositeView, ComplexEView, Editor
{
   private ComplexEObject _ceo;

   private transient CommandsButtonView _cmdsView;
   private FormView _formView;
   private StatusPanel _statusPanel;

   public TopLevelFormView(ComplexEObject ceo)
   {
      _ceo = ceo;

      setLayout(new BorderLayout());

      _formView = new FormView(ceo);
      add(_formView, BorderLayout.CENTER);

      _cmdsView = new CommandsButtonView();
      _cmdsView.bind(_ceo, this, BorderLayout.EAST, this);

      add(statusPanel(), BorderLayout.SOUTH);
   }


   public void addNotify()
   {
      super.addNotify();
      // this sucks but it works (turns out to be known bug, posted
      //  against java1.4.2)
      new Thread()
      {
         public void run()
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _formView.focusFirstEditableField();
               }
            });
         }
      }.start();
   }

   private JPanel statusPanel()
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
   public int validateValue() { return _formView.validateValue(); }

   public void setEditable(boolean editable)
   {
      _formView.setEditable(editable);
      if (_ceo.isEditableState())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _formView.focusFirstEditableField();
            }
         });
      }
   }

   public boolean isEditable() { return _formView.isEditable(); }

   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return false; }
   public EView getInnerView() { return _formView; }
}
