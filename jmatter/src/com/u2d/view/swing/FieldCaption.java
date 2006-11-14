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

      _cmdsView.bind(_field, this);
      
      _font = getFont();
      _foreground = getForeground();

      setupLabel();
      setLabelFor(comp);
      setupTooltip();
      setupMnemonic();
      styleIfRequired();
      
      _field.getMnemonic().addChangeListener(this);
      _field.getRequired().addChangeListener(this);
      _field.getDescription().addChangeListener(this);
      _field.getLabel().addChangeListener(this);
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

   private void setupLabel()
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
         
         char mnemonic = Character.toLowerCase(_field.mnemonic());
         int index = text.indexOf(mnemonic);
         if (index < 0)
         {
            mnemonic = Character.toUpperCase(_field.mnemonic());
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

   public void propertyChange(final PropertyChangeEvent evt)
   {
   }
   
   public void stateChanged(final ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (evt.getSource().equals(_field.getMnemonic()))
            {
               setupMnemonic();
            }
            else if (evt.getSource().equals(_field.getRequired()))
            {
               styleIfRequired();
            }
            else if (evt.getSource().equals(_field.getDescription()))
            {
               setupTooltip();
            }
            else if (evt.getSource().equals(_field.getLabel()))
            {
               setupLabel();
            }
            revalidate(); repaint();
         }
      });
   }

   public boolean isMinimized() { return true; }

   public void detach()
   {
      _field.getMnemonic().removeChangeListener(this);
      _field.getRequired().removeChangeListener(this);
      _field.getDescription().removeChangeListener(this);
      _field.getLabel().removeChangeListener(this);
   }
}
