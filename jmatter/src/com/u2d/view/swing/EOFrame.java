/*
 * Created on Dec 12, 2003
 */
package com.u2d.view.swing;

import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.GradientPanel;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.view.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Eitan Suez
 */
public class EOFrame extends CloseableJInternalFrame
         implements ComplexEView, Editor, CompositeView, RootView
{
   private transient EView _view;
   private transient TitleView _titleView;
   private ComplexEObject _ceo;
   private transient StatusPanel _statusPanel;
   private transient ChangeListener _sizeUpdater;
   
   public EOFrame(EView view)
   {
      super();
      setResizable(true); setMaximizable(true); setIconifiable(true); setClosable(true);

      _view = view;
      _ceo = ((ComplexEObject) _view.getEObject());
      
      _ceo.addChangeListener(this);
      _ceo.addPropertyChangeListener(this);
      _sizeUpdater = new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            SwingUtilities.invokeLater(new Runnable() {
               public void run()
               {
                  updateSize();
               }
            });
         }
      };
      _ceo.addPostChangeListener(_sizeUpdater);
      
      if (_ceo.isTransientState())
         setTitle("New "+_ceo.type().getNaturalName());
      else
         setTitle(_ceo.title().toString());
      setFrameIcon(_ceo.iconSm());

      buildHeaderPanel();
      JPanel contentPane = (JPanel) getContentPane();
      
      // centerpane bundles a little message area (north) and the main view (center)
      JPanel centerPane = new JPanel(new BorderLayout());
      _statusPanel = new StatusPanel();
      centerPane.add(_statusPanel, BorderLayout.NORTH);
      
      centerPane.add((JComponent) _view, BorderLayout.CENTER);
      contentPane.add(centerPane, BorderLayout.CENTER);
      
      setupToFocusOnDragEnter();
      pack();
   }
   
   private void buildHeaderPanel()
   {
      JPanel contentPane = (JPanel) getContentPane();
      _titleView = new TitleView(_ceo, this);

      FormLayout layout =  new FormLayout("left:pref:grow, right:pref", 
                                          "bottom:pref");
      
      GradientPanel gpanel = new GradientPanel(_ceo.type().colorCode(), false);
      PanelBuilder builder = new PanelBuilder(layout, gpanel);
      CellConstraints cc = new CellConstraints();
      builder.add(_titleView, cc.xy(1, 1));
      
      AlternateView altView = getAlternateView();
      if (altView != null)
      {
         builder.add(altView.getControlPane(), cc.xy(2, 1));
      }
      
      JPanel topPanel = builder.getPanel();
      contentPane.add(topPanel, BorderLayout.NORTH);
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
      _ceo.removePostChangeListener(_sizeUpdater);
      _ceo.removePropertyChangeListener(this);
      _ceo.removeValidationListener(_statusPanel);
      _titleView.detach();
      _view.detach();
      // keyboardfocusmanager will hold a reference to eoframe preventing it from
      // begin garbage-collected, thus:
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRoot(null);
   }
   
   public EView getView() { return _view; }
   
   public void propertyChange(PropertyChangeEvent evt) {}

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
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
      });
   }
   
   public EObject getEObject() { return _view.getEObject(); }
   public boolean isMinimized() { return false; }


   class StatusPanel extends JPanel implements ValidationListener
   {
      JLabel _statusLabel = msgLabel();
      JLabel _msgLabel = msgLabel();
      
      StatusPanel()
      {
         setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
         add(_statusLabel);
         add(_msgLabel);

         _ceo.addValidationListener(this);
      }
      
      private JLabel msgLabel()
      {
         JLabel label = new JLabel("");
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
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
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
               
               updateSize();
            }
         });
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
   
   
   private AlternateView getAlternateView()
   {
      EView innerView = _view;
      while (innerView != null &&
             (innerView instanceof CompositeView) &&
             !(innerView instanceof AlternateView) )
      {
         innerView = ((CompositeView) innerView).getInnerView();
      }
      
      if (innerView instanceof AlternateView)
         return (AlternateView) innerView;
      
      return null;
   }

}
