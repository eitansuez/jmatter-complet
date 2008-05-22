package com.u2d.view.swing.atom;

import com.u2d.ui.CardPanel;
import com.u2d.view.AtomicEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.model.*;
import com.u2d.field.AtomicField;
import com.u2d.field.CompositeField;
import com.u2d.validation.ValidationListener;
import com.u2d.validation.ValidationEvent;
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
            if (_eo.field() != null)
            {
               errors += _eo.field().validate(_eo.parentObject());
            }
            if (errors == 0)
            {
               if (_previousErrors != 0)
               {
                  _eo.fireValidationException(""); // reset previous
               }
               else
               {
                  colorBackground(editorComponent(), "", _eo);
               }
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
         setEditable(_eo.parentObject().isEditableState() && !_eo.field().isReadOnly()
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
            setEditable(!_eo.field().isReadOnly());
         }
      };
      if (_eo.field() != null)
      {
         _eo.field().addPropertyChangeListener("readOnly", readOnlyListener);
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
      if (field == null)
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
         _eo.field().removePropertyChangeListener(readOnlyListener);
      }
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


   // common utility for multiple kinds of editors/renderers ..

   public static void colorBackground(JComponent component, ValidationEvent evt)
   {
      colorBackground(component, evt.getMsg(), evt.getSource());
   }
   public static void colorBackground(JComponent component, String msg, Object source)
   {
      boolean emptyMsg = StringEO.isEmpty(msg);
      if (!emptyMsg)
      {
         component.setBackground(ValidationEvent.INVALID_COLOR);
         component.setToolTipText(msg);
         return;
      }
      
      component.setToolTipText(null);

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

}
