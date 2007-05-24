package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.field.Association;
import com.u2d.model.EObject;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 27, 2006
 * Time: 4:03:01 PM
 */
public class AssociationEO extends JPanel implements ComplexEView
{
   private AssociationEditor _editor;
   private Association _association;

   public AssociationEO(Association association)
   {
      setLayout(new FlowLayout(FlowLayout.LEFT));
      setOpaque(false);

      _association = association;
      _editor = new AssociationEditor(_association.field());

      if (_association.isEmpty())
      {
         _editor.clearValue();
      }
      else
      {
         _editor.renderValue(_association.get());
      }
      add(_editor);
   }
   
   Insets _insets = new Insets(0, 0, 0, 0);
   public Insets getInsets() { return _insets; }

   public void bindValue() { _editor.bindValue(_association); }

   public Association getAssociation() { return _association; }
   public EObject getEObject() { return _association.get(); }
   public boolean isMinimized() { return false; }
   public void detach() { _editor.detach(); }
   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }

   public void addActionListener(ActionListener l)
   {
      _editor.addActionListener(l);
   }
   public void removeActionListener(ActionListener l)
   {
      _editor.removeActionListener(l);
   }
   
}
