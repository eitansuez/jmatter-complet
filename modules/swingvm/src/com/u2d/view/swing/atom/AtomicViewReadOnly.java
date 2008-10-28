package com.u2d.view.swing.atom;

import com.u2d.view.AtomicEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.model.*;
import com.u2d.field.AtomicField;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * Date: Jun 8, 2005
 * Time: 1:05:45 PM
 *
 * @author Eitan Suez
 */
public class AtomicViewReadOnly extends JPanel implements AtomicEView
{
   protected AtomicEObject _eo;
   protected AtomicRenderer _renderer;
   protected transient CommandsContextMenuView _cmdsView;

   public AtomicViewReadOnly()
   {
      setLayout(new BorderLayout());
      _cmdsView = new CommandsContextMenuView();
   }
   public AtomicViewReadOnly(AtomicEObject eo)
   {
      this();
      bind(eo);
   }
   public AtomicViewReadOnly(AtomicEObject eo, AtomicRenderer renderer)
   {
      this();
      bind(eo, renderer);
   }

   public void bind(AtomicEObject eo)
   {
      bind(eo, null);
   }
   public void bind(AtomicEObject eo, AtomicRenderer specifiedRenderer)
   {
      _eo = eo;

      setupRenderer(eo);
      if (specifiedRenderer != null) _renderer = specifiedRenderer;

      JComponent rendererComponent = (JComponent) _renderer;
      add(rendererComponent, BorderLayout.CENTER);

      _cmdsView.bind(_eo, this);

      if (_eo.parentObject() != null)
         _eo.parentObject().addChangeListener(this);
      _eo.addChangeListener(this);

      stateChanged(null);
   }

   private void setupRenderer(AtomicEObject eo)
   {
      AtomicField field = (AtomicField) eo.field();
      if (field == null || _eo.parentObject() == null)
      {
         _renderer = eo.getRenderer();
      }
      else
      {
         _renderer = field.getRenderer(_eo.parentObject());
      }
   }


   public void detach()
   {
      _eo.removeChangeListener(this);
      if (_eo.parentObject() != null)
         _eo.parentObject().removeChangeListener(this);

      _cmdsView.detach();

      removeAll();  // precaution
   }

   public void stateChanged(ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _renderer.render(_eo);
         }
      });
   }

   public EObject getEObject() { return _eo; }

}