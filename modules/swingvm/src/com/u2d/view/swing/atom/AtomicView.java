package com.u2d.view.swing.atom;

import com.u2d.ui.CardPanel;
import com.u2d.view.AtomicEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.model.*;
import com.u2d.field.AtomicField;
import com.u2d.field.CompositeField;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
import com.u2d.validation.Required;
import com.u2d.type.atom.StringEO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Date: Jun 8, 2005
 * Time: 1:05:45 PM
 *
 * @author Eitan Suez
 */
public class AtomicView extends CardPanel implements AtomicEView, Editor, ValidationListener
{
   protected AtomicEObject _eo;
   protected AtomicRenderer _renderer;
   protected AtomicEditor _editor;
   protected FocusAdapter _focusAdapter;
   protected transient CommandsContextMenuView _cmdsView;

   protected int _previousErrors = 0;
   private PropertyChangeListener readOnlyListener;

   public AtomicView()
   {
      _focusAdapter = new FocusAdapter()
      {
         public void focusLost(FocusEvent e)
         {
            int errors = _editor.bind(_eo);
            if (errors == 0)
            {
               if (_eo.field() != null)
               {
                  errors += _eo.field().validate(_eo.parentObject());
               }
               if (_previousErrors != 0)
               {
                  _eo.fireValidationException(""); // reset previous
               }
               colorBackground(editorComponent(), "", _eo);
            }
            _previousErrors = errors;
         }
      };
      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(AtomicEObject eo)
   {
      bind(eo, null, null);
   }
   public void bind(AtomicEObject eo, AtomicRenderer specifiedRenderer)
   {
      bind(eo, specifiedRenderer, null);
   }
   public void bind(AtomicEObject eo, AtomicRenderer specifiedRenderer, AtomicEditor specifiedEditor)
   {
      _eo = eo;

      setupRendererAndEditor(eo);
      if (specifiedRenderer != null) _renderer = specifiedRenderer;
      if (specifiedEditor != null) _editor = specifiedEditor;

      JComponent rendererComponent = (JComponent) _renderer;
      JComponent editorComponent = (JComponent) _editor;

      if (!isTextComponent(editorComponent))
      {
         editorComponent.setOpaque(false);
      }

      add(rendererComponent, "view");
      add(editorComponent, "edit");

      if (_eo.parentObject() != null)
      {
         boolean readOnly = _eo.field().isReadOnly() || _eo.isReadOnly();
         setEditable(_eo.parentObject().isEditableState() && !readOnly
           && !(((CompositeField) _eo.field()).isIdentity() && !_eo.parentObject().isTransientState()) );
      }
      else
      {
         setEditable(false);  // start out read-only by default
      }

      _cmdsView.bind(_eo, this);

      if (_eo.parentObject() != null)
         _eo.parentObject().addChangeListener(this);
      _eo.addChangeListener(this);
      _eo.addValidationListener(this);

      stateChanged(null);

      JComponent actualEditor = editorComponent();
      if (isTextComponent(actualEditor) && eo.field() != null && eo.field().required() && eo.isEmpty())
      {
         actualEditor.setBackground(ValidationEvent.REQUIRED_COLOR);
      }
      actualEditor.addFocusListener(_focusAdapter);

      readOnlyListener = new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            boolean readOnly = _eo.field().isReadOnly() || _eo.isReadOnly();
            setEditable(!readOnly);
         }
      };
      if (_eo.field() != null)
      {
         _eo.field().addPropertyChangeListener("readOnly", readOnlyListener);
         _eo.addPropertyChangeListener("readOnly", readOnlyListener);
      }
   }

   private JComponent editorComponent()
   {
      JComponent editorComponent = (JComponent) _editor;
      if (editorComponent instanceof CompositeEditor)
      {
         editorComponent = ((CompositeEditor) editorComponent).getEditorComponent();
      }
      return editorComponent;
   }

   private void setupRendererAndEditor(AtomicEObject eo)
   {
      AtomicField field = (AtomicField) eo.field();
      if (field == null || _eo.parentObject() == null)
      {
         _renderer = eo.getRenderer();
         _editor = eo.getEditor();
      }
      else
      {
         _renderer = field.getRenderer(_eo.parentObject());
         if (field.hasValueOptions())
         {
            _editor = new ComboEditor(eo.field().valueOptions());
         }
         else
         {
            _editor = field.getEditor(_eo.parentObject());
         }
      }
   }


   public void detach()
   {
      _eo.removeChangeListener(this);
      if (_eo.parentObject() != null)
         _eo.parentObject().removeChangeListener(this);

      ((JComponent) _editor).removeFocusListener(_focusAdapter);

      _eo.removeValidationListener(this);
      _cmdsView.detach();
      if (_eo.field() != null)
      {
         _eo.field().removePropertyChangeListener("readOnly", readOnlyListener);
      }
      _eo.removePropertyChangeListener("readOnly", readOnlyListener);

      removeAll();  // precaution
      // noticing some strange references held on stringeditor such as CompositionAreaHandler
   }


   private boolean isTextComponent(JComponent editorComponent)
   {
      return (editorComponent instanceof JTextComponent);
   }

   public void stateChanged(ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _renderer.render(_eo);
            _editor.render(_eo);
         }
      });
   }

   public void validationException(final ValidationEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (_renderer instanceof ValidationListener)
            {
               ((ValidationListener) _renderer).validationException(evt);
            }
            if (_editor instanceof ValidationListener)
            {
               ((ValidationListener) _editor).validationException(evt);
            }
         }
      });
   }

   public int transferValue()
   {
      int errorCount = _editor.bind(_eo);
      _renderer.render(_eo);
      return errorCount;
   }

   public int validateValue() { return _eo.validate(); }

   
   private boolean _editable = false;
   public void setEditable(boolean editable)
   {
      _editable = editable;
      show((editable) ? "edit" : "view");
   }
   public boolean isEditable() { return _editable; }

   public EObject getEObject() { return _eo; }
   
   public AtomicEditor getEditor() { return _editor; }
   public AtomicRenderer getRenderer() { return _renderer; }


   // common utility for multiple kinds of editors/renderers ..

   public static boolean toggleValidationClientProperty(JComponent component, ValidationEvent evt)
   {
      return toggleValidationClientProperty(component, evt.getMsg(), evt.getSource());
   }
   private static boolean toggleValidationClientProperty(JComponent component, String msg, Object source)
   {
      boolean emptyMsg = StringEO.isEmpty(msg);
      boolean failedValidation = !(emptyMsg || Required.MSG.equals(msg));
      component.putClientProperty(ValidationEvent.FAILED_VALIDATION, failedValidation);
      String toolTipText = failedValidation ? msg : null;
      component.setToolTipText(toolTipText);
      return failedValidation;
   }
   
   public static void colorBackground(JComponent component, ValidationEvent evt)
   {
      colorBackground(component, evt.getMsg(), evt.getSource());
   }
   private static void colorBackground(JComponent component, String msg, Object source)
   {
      boolean failedValidation = toggleValidationClientProperty(component, msg, source);
      if (failedValidation) return;

      Color bgColor = ValidationEvent.normalColor(component);
      if (source instanceof EObject)
      {
         EObject eo = (EObject) source;
         if (eo.field() != null && eo.field().required() && eo.isEmpty())
         {
            bgColor = ValidationEvent.REQUIRED_COLOR;
         }
      }
      component.setBackground(bgColor);
   }

   public static void decorateComponentForValidation(Graphics g, JComponent c, boolean placeLeft)
   {
      Object propValue = c.getClientProperty(ValidationEvent.FAILED_VALIDATION);
      if (propValue == null) return;
      boolean passed = ! ((Boolean) propValue);
      if (passed) return;

      paintErrorIcon(g, c, placeLeft);
   }

   private static Color redColor = new Color(0xD71E17);
   private static Stroke redStroke = new BasicStroke(1);
   public static void decorateBorderForValidation(Graphics g, JComponent c)
   {
      Object propValue = c.getClientProperty(ValidationEvent.FAILED_VALIDATION);
      if (propValue == null) return;
      boolean passed = ! ((Boolean) propValue);
      if (passed) return;

      Graphics2D g2 = (Graphics2D) g;
      Color savedColor = g2.getColor();
      Stroke savedStroke = g2.getStroke();

      g2.setColor(redColor);
      g2.setStroke(redStroke);

      g2.drawRect(0, 0, c.getWidth()-1, c.getHeight()-1);

      g2.setColor(savedColor);
      g2.setStroke(savedStroke);
   }

   private static void paintErrorIcon(Graphics g, Component c, boolean placeLeft)
   {
      int padding = 5;
      int x = placeLeft ? padding : c.getWidth() - padding - ICON_WIDTH;
      int y = (c.getHeight() - ICON_HEIGHT) / 2 + 1;
      g.drawImage(ERROR_ICON.getImage(), x, y, ICON_WIDTH, ICON_HEIGHT, ERROR_ICON.getImageObserver());
   }
   static ImageIcon ERROR_ICON;
   static int ICON_WIDTH, ICON_HEIGHT;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/error_co.gif");
      ERROR_ICON = new ImageIcon(imgURL);
      ICON_WIDTH = ERROR_ICON.getIconWidth();
      ICON_HEIGHT = ERROR_ICON.getIconHeight();
   }

}
