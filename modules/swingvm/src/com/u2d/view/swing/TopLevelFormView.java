package com.u2d.view.swing;

import com.u2d.element.Field;
import com.u2d.model.AtomicEObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.view.ComplexEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.swing.list.CommandsButtonView;
import org.jdesktop.swingx.JXPanel;
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
public class TopLevelFormView extends JXPanel
      implements CompositeView, ComplexEView, Editor
{
   private ComplexEObject _ceo;

   private transient CommandsButtonView _cmdsView;
   private IFormView _formView;
   private StatusPanel _statusPanel;

   public TopLevelFormView(ComplexEObject ceo)
   {
      _ceo = ceo;
      init(new FormView(ceo));
   }
   public TopLevelFormView(IFormView customFormView)
   {
      _ceo = (ComplexEObject) customFormView.getEObject();
      init(customFormView);
   }

   private void init(IFormView formview)
   {
      setLayout(new BorderLayout());

      _formView = formview;
      add((JComponent) _formView, BorderLayout.CENTER);

      _cmdsView = new CommandsButtonView();
      // don't bother to allocate space if no commands.  this can be the case if edit and delete restrictions
      // have been added for a given role..
      if (_cmdsView.hasCommandsFor(_ceo, this))
      {
         _cmdsView.bind(_ceo, this, BorderLayout.LINE_END, this);
      }

      addStatusPanel();
   }

   public void addNotify()
   {
      super.addNotify();
      // this sucks but it works (turns out to be known bug, posted
      //  against java1.4.2)
      AppLoader.getInstance().newThread(new Runnable()
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
      }).start();
   }

   private void addStatusPanel()
   {
      if (_ceo.isMeta())
         return;
      
      _statusPanel = new StatusPanel();
      _statusPanel.addEO(_ceo.getCreatedOn());
      Field statusField = _ceo.field("status");
      if (statusField != null)
      {
         _statusPanel.addEO((AtomicEObject) statusField.get(_ceo));
      }
      add(_statusPanel, BorderLayout.PAGE_END);
   }

   public void detach()
   {
      _formView.detach();
      _cmdsView.detach();
      if (_statusPanel != null) _statusPanel.detach();
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
