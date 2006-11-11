/*
 * Created on Dec 14, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

import com.u2d.element.Field;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.model.EObject;

/**
 * @author Eitan Suez
 */
public class FieldCaption extends com.u2d.ui.Caption implements ComplexEView
{
   private static Font BOLD_FONT;
   static
   {
      BOLD_FONT = UIManager.getFont("Label.font").deriveFont(Font.BOLD);
   }
   
   private Field _field;
   private transient CommandsContextMenuView _cmdsView = new CommandsContextMenuView();
   private transient Font _font;
   private transient Color _foreground;
   

   public FieldCaption(Field field, JComponent comp)
   {
      _field = field;

      _field.addPropertyChangeListener(this);
      _cmdsView.bind(_field, this);
      
      _font = getFont();
      _foreground = getForeground();

      setupLabelAppender();
      setLabelFor(comp);
      setupTooltip();
      setupMnemonic();
      styleIfRequired();
   }

   private void styleIfRequired()
   {
      if (_field.required())
      {
         setFont(BOLD_FONT);
         setForeground(Color.BLUE);
      }
      else
      {
         setFont(_font);
         setForeground(_foreground);
      }
   }

   private void setupLabelAppender()
   {
      Class cls = _field.getJavaClass();
      String appender = (cls.equals(BooleanEO.class)) ? "?" : ":";
      setText(_field.label()+appender);
   }

   private void setupTooltip()
   {
      if (!_field.getDescription().isEmpty())
         setToolTipText(_field.description());
   }

   private void setupMnemonic()
   {
      if (_field.hasMnemonic())
      {
         String text = _field.label();
         
         char mnemonic = Character.toLowerCase(_field.getMnemonic());
         int index = text.indexOf(mnemonic);
         if (index < 0)
         {
            mnemonic = Character.toUpperCase(_field.getMnemonic());
            index = text.indexOf(mnemonic);
         }
         
         if (index >= 0)
         {
            setDisplayedMnemonic(mnemonic);
            setDisplayedMnemonicIndex(index);
         }
      }
   }

   public EObject getEObject() { return _field; }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if ("mnemonic".equals(evt.getPropertyName()))
      {
         setupMnemonic();
      }
      if ("required".equals(evt.getPropertyName()))
      {
         styleIfRequired();
      }
      if ("description".equals(evt.getPropertyName()))
      {
         setupTooltip();
      }
   }
   
   public void stateChanged(ChangeEvent e)
   {
   }

   public boolean isMinimized() { return true; }

   public void detach()
   {
      _field.removePropertyChangeListener(this);
   }
}
