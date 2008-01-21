package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.field.Association;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import static com.u2d.pubsub.AppEventType.DELETE;
import com.u2d.css4swing.style.ComponentStyle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 1:04:42 PM
 */
public class SimpleAssociationView extends JPanel implements ComplexEView, AppEventListener
{
   private Association _association;
   private IconView iconView = new IconView();

   public SimpleAssociationView()
   {
      setOpaque(false);
      setLayout(new BorderLayout());
      add(iconView, BorderLayout.CENTER);
      ComponentStyle.setIdent(iconView, "command-icon-view");
   }
   public SimpleAssociationView(Association a)
   {
      this();
      bind(a);
   }

   public void bind(Association a)
   {
      if (_association != null)
         detach();
      _association = a;
      _association.addPropertyChangeListener(this);
      bindIconView();
   }
   public void detach()
   {
      _association.removePropertyChangeListener(this);
      ComplexEObject value = (ComplexEObject) getEObject();
      if (!value.isEmpty())
      {
         value.removeAppEventListener(DELETE, this);
      }
      iconView.detach();
   }
   
   public void clear()
   {
      _association.set(null);
   }
   
   public void propertyChange(PropertyChangeEvent evt)
   {
      if (_association.getName().equals(evt.getPropertyName()))
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               bindIconView();
            }
         });
      }
   }
   
   private void bindIconView()
   {
      iconView.detach();
      ComplexEObject value = (ComplexEObject) getEObject();
      if (!value.isEmpty())
      {
         value.addAppEventListener(DELETE, this);
      }
      iconView.bind(value);
   }

   public void onEvent(AppEvent evt)
   {
      ((ComplexEObject) iconView.getEObject()).removeAppEventListener(DELETE, this);
      _association.set(null);
   }

   public void stateChanged(ChangeEvent e) { }

   public Association getAssociation() { return _association; }
   public EObject getEObject() { return _association.get(); }
   public boolean isMinimized() { return false; }
}
