package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.CompositeView;
import com.u2d.view.RootView;
import com.u2d.view.EView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.ImageIcon;
import java.beans.PropertyChangeEvent;
import org.wings.*;
import java.awt.Color;

/**
 * @author Eitan Suez
 */
public class EOFrame extends SInternalFrame
         implements ComplexEView, Editor, CompositeView, RootView
{
   private transient EView _view;
   private transient TitleView _titleView;
   private ComplexEObject _ceo;
   private transient StatusPanel _statusPanel;

   public EOFrame(EView view)
   {
      super();
//      setResizable(true);
      setIconifyable(true);
      setMaximizable(true); setClosable(true);

      _view = view;
      _ceo = ((ComplexEObject) _view.getEObject());

      _ceo.addChangeListener(this);
      _ceo.addPropertyChangeListener(this);

      if (_ceo.isTransientState())
         setTitle("New "+_ceo.type().getNaturalName());
      else
         setTitle(_ceo.title().toString());
      setIcon(new SImageIcon((ImageIcon) _ceo.iconSm()));

      SContainer contentPane = getContentPane();
      _titleView = new TitleView(_ceo, this);
      contentPane.add(_titleView, SBorderLayout.NORTH);

      // centerpane bundles a little message area (north) and the main view (center)
      SPanel centerPane = new SPanel(new SBorderLayout());
      _statusPanel = new StatusPanel();
      centerPane.add(_statusPanel, SBorderLayout.NORTH);

      centerPane.add((SComponent) _view, SBorderLayout.CENTER);
      contentPane.add(centerPane, SBorderLayout.CENTER);

//      setupToFocusOnDragEnter();
//      pack();
   }

   public void dispose()
   {
      super.dispose();
      detach();
   }

   public void detach()
   {
      _ceo.cancelTransition();
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
      _ceo.removeValidationListener(_statusPanel);
      _titleView.detach();
      _view.detach();
   }

   public EView getView() { return _view; }

   public void propertyChange(PropertyChangeEvent evt) {}

   public void stateChanged(ChangeEvent evt)
   {
      if (_ceo.isNullState())
      {
         close();
         return;
      }

      if (!_ceo.isEditableState())
      {
         setTitle(_ceo.title().toString());
      }
   }

   public EObject getEObject() { return _view.getEObject(); }
   public boolean isMinimized() { return false; }


   class StatusPanel extends SPanel implements ValidationListener
   {
      SLabel _statusLabel = msgLabel();
      SLabel _msgLabel = msgLabel();

      StatusPanel()
      {
         setLayout(new SFlowLayout(SFlowLayout.LEFT));
         add(_statusLabel);
         add(_msgLabel);

         _ceo.addValidationListener(this);
      }

      private SLabel msgLabel()
      {
         SLabel label = new SLabel("");
         label.setFont(ValidationNoticePanel.ITALIC_FONT);
         label.setForeground(Color.RED);
         return label;
      }

      private void reset()
      {
         _statusLabel.setText("");
         _msgLabel.setText("");
      }

      public void validationException(final ValidationEvent evt)
      {
         if ("".equals(evt.getMsg()))
         {
            reset();
            return;
         }

         if (evt.isStatusMsg())
         {
            _statusLabel.setText(evt.getMsg());
         }
         else
         {
            _msgLabel.setText(evt.getMsg());
         }
      }

   }

   public int transferValue() { return ((Editor) _view).transferValue(); }
   public void setEditable(boolean editable)
   {
      ((Editor) _view).setEditable(editable);
      if (!isEditable())
         _statusPanel.reset();
   }
   public boolean isEditable() { return ((Editor) _view).isEditable(); }

   public EView getInnerView() { return _view; }

   public void close() { dispose(); }

}
