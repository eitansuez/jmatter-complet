package com.u2d.view.swing;

import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.ui.GradientPanel;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.ValidationListener;
import com.u2d.view.*;
import com.u2d.app.PersistenceMechanism;
import com.u2d.app.Context;
import com.u2d.css4swing.style.ComponentStyle;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 5, 2006
 * Time: 5:55:00 PM
 * @author Eitan Suez
 */
public class EOPanel extends JPanel
      implements ComplexEView, Editor, CompositeView
{
   private transient EView _view;
   private transient TitleView _titleView;
   private ComplexEObject _ceo;
   private transient StatusPanel _statusPanel;
   private transient ChangeListener _sizeUpdater;
   
   public EOPanel(EView view)
   {
      _view = view;
      _ceo = ((ComplexEObject) _view.getEObject());
      
      setLayout(new BorderLayout());
      buildHeaderPanel();
      
      // centerpane bundles a little message area (north) and the main view (center)
      JPanel centerPane = new JPanel(new BorderLayout());
      _statusPanel = new StatusPanel();
      centerPane.add(_statusPanel, BorderLayout.NORTH);
      
      centerPane.add((JComponent) _view, BorderLayout.CENTER);
      add(centerPane, BorderLayout.CENTER);
      
      _sizeUpdater = new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            SwingUtilities.invokeLater(new Runnable() {
               public void run()
               {
                  CloseableJInternalFrame.updateSize(EOPanel.this);
               }
            });
         }
      };
      _ceo.addChangeListener(this);
      _ceo.addPostChangeListener(_sizeUpdater);
   }
   
   private void buildHeaderPanel()
   {
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
      ComponentStyle.addClass(topPanel, "instance-title-panel");
      add(topPanel, BorderLayout.NORTH);
   }
   
   public void detach()
   {
      _ceo.cancelTransition();
      _ceo.removeValidationListener(_statusPanel);
      _titleView.detach();
      _view.detach();
      _ceo.removeChangeListener(this);
      _ceo.removePostChangeListener(_sizeUpdater);
      // keyboardfocusmanager will hold a reference to eoframe preventing it from
      // begin garbage-collected, thus:
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRoot(null);
   }
   
   public EView getView() { return _view; }
   
   public void propertyChange(PropertyChangeEvent evt) {}

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      if (!_ceo.isEditableState())
         _statusPanel.reset();
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
         ComponentStyle.addClass(label, "validation-msg");
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
            }
         });
         _sizeUpdater.stateChanged(null);
      }
      
   }
   
   public int transferValue() { return ((Editor) _view).transferValue(); }
   public int validateValue() { return ((Editor) _view).validateValue(); }

   public void setEditable(boolean editable)
   {
      ((Editor) _view).setEditable(editable);
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
   
   public void serialize(XMLEncoder enc)
   {
      if (_ceo.isTransientState()) return;
      enc.writeObject(EOPanel.class);
      enc.writeObject(_ceo.type().getJavaClass());
      enc.writeObject(_ceo.getID());
   }
   
   public static void deserialize(final XMLDecoder dec, final FlexiFrame f)
   {
      final Class ceoType = (Class) dec.readObject();
      final Long id = (Long) dec.readObject();
      new Thread()
      {
         public void run()
         {
            PersistenceMechanism pmech = Context.getInstance().getPersistenceMechanism();
            final ComplexEObject ceo = pmech.load(ceoType, id);
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  f.addView(new EOPanel(ceo.getMainView()));
               }
            });
         }
      }.start();
   }
}
