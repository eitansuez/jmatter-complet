package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.field.Association;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import org.jdesktop.swingx.JXPanel;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 1:04:42 PM
 */
public class SimpleAssociationView extends JXPanel
      implements ComplexEView
{
   private Association _association;
   private IconView iconView = new IconView();

   public SimpleAssociationView()
   {
      init();
   }
   public SimpleAssociationView(Association a)
   {
      init();
      bind(a);
   }
   
   private void init()
   {
      setOpaque(false);
      setLayout(new GridLayout(1,1));
      add(iconView);
   }

   public void bind(Association a)
   {
      if (_association != null)
      {
         detach();
      }
      _association = a;
      _association.addPropertyChangeListener(this);
      bindIconView();
   }
   public void detach()
   {
      _association.removePropertyChangeListener(this);
      iconView.detach();
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
      iconView.bind(value);
   }

   public void stateChanged(ChangeEvent e) { }

   public Association getAssociation() { return _association; }
   public EObject getEObject() { return _association.get(); }
   public boolean isMinimized() { return false; }
}
