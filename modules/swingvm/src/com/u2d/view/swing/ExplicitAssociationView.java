package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.find.FindPanel;
import com.u2d.field.Association;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.list.CriteriaListEO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 3:30:26 PM
 */
public class ExplicitAssociationView extends JPanel implements ComplexEView
{
   private Association _association;
   private EView _assocView, _paginableView;

   public ExplicitAssociationView(Association association)
   {
      this(association, association.type());
   }
   
   /*
    * have two constructors in order to be able to constrain the 
    * association type to a specific/concrete type when the association
    * is to an abstract type / interface.
    */
   public ExplicitAssociationView(Association association, ComplexType type)
   {
      _association = association;

      _assocView = new AssociationView2(_association);

      CriteriaListEO leo = (CriteriaListEO) type.Browse(null);
      leo.setPickState(_association, true);
      
      _paginableView = leo.getAlternateView();
      
      JPanel findPanel = new FindPanel(leo);
      JPanel combo = new JPanel(new BorderLayout());
      combo.add(findPanel, BorderLayout.NORTH);
      combo.add((JComponent) _paginableView, BorderLayout.CENTER);

      setLayout(new BorderLayout());
      add((JComponent) _assocView, BorderLayout.NORTH);
      add((JComponent) combo, BorderLayout.CENTER);
   }

   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }

   public Association getAssociation() { return _association; }
   public EObject getEObject() { return _association.get(); }

   public boolean isMinimized() { return false; }

   public void detach()
   {
      _assocView.detach();
      _paginableView.detach();
   }

}
