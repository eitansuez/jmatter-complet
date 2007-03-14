/*
 * Created on Apr 5, 2005
 */
package com.u2d.view.swing.find;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;
import com.u2d.element.Field;
import com.u2d.find.FieldPath;
import com.u2d.find.Inequality;
import com.u2d.find.QuerySpecification;
import com.u2d.find.Searchable;
import com.u2d.find.SimpleQuery;
import com.u2d.find.inequalities.IdentityInequality;
import com.u2d.model.*;
import com.u2d.ui.JComboTree;
import com.u2d.view.EView;
import com.u2d.view.swing.atom.AtomicView;
import com.u2d.view.swing.atom.StringEditor;

/**
 * @author Eitan Suez
 */
public class FieldFilter extends JPanel
{
   private ComplexType _type;
   private JComboTree _fieldCombo;
   private JComboBox _ineqCombo;
   private java.util.List _inequalities;
   private JPanel _valueSpot = new JPanel();
   {
      _valueSpot.setOpaque(false);
   }

   public FieldFilter(ComplexType type)
   {
      _type = type;

      setOpaque(false);
      setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));

      add(fieldCombo());
      add(ineqCombo());
      add(_valueSpot);

      if (_type.hasDefaultSearchPath())
      {
         FieldPath fieldPath = new FieldPath(_type.defaultSearchPath());
         _fieldCombo.setSelectedPath(fieldPath.getPathList());
      }
      else
      {
         _fieldCombo.selectFirst();
      }
   }

   public FieldFilter(ComplexType type, QuerySpecification spec)
   {
      this(type);
      bind(spec);
   }

   private void bind(QuerySpecification spec)
   {
      _fieldCombo.setSelectedPath(spec.getFieldPath().getPathList());

      Inequality ineq = null;
      for (int i=0; i<_ineqCombo.getItemCount(); i++)
      {
         ineq = (Inequality) _ineqCombo.getItemAt(i);
         if (ineq.equals(spec.getInequality()))
         {
            _ineqCombo.setSelectedItem(ineq);
            break;
         }
      }

      EObject eo = ineq.getValueEditor().getEObject();
      if (eo instanceof NullAssociation)
      {
         ((NullAssociation) eo).set((ComplexEObject) spec.getValue());
      }
      else
      {
         eo.setValue(spec.getValue());
      }
   }

   private JComponent fieldCombo()
   {
      _fieldCombo = new JComboTree(_type.searchTreeModel());
      _fieldCombo.addActionListener(_fieldActionListener);
      return _fieldCombo;
   }

   private JComponent ineqCombo()
   {
      _ineqCombo = new JComboBox();
      _ineqCombo.setOpaque(false);
      _ineqCombo.addActionListener(_ineqActionListener);
      return _ineqCombo;
   }

   ActionListener _fieldActionListener = new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            java.util.List inequalities =
                  determineInequalities(_fieldCombo.getSelectedItem());
            if (inequalities == null) return;

//            if (inequalities.equals(_inequalities))
//            {
//               return;
//            }

            _inequalities = inequalities;
            updateIneqCombo();
         }
      };

   private void updateIneqCombo()
   {
      _ineqCombo.removeAllItems();
      for (int i = 0; i < _inequalities.size(); i++)
      {
         _ineqCombo.addItem(_inequalities.get(i));
      }
      _ineqCombo.requestFocusInWindow();
      if (_ineqCombo.getItemCount() > 0) _ineqCombo.setSelectedIndex(0);
      com.u2d.ui.desktop.CloseableJInternalFrame.updateSize(this);
   }


   private java.util.List determineInequalities(Object item)
   {
      if (item instanceof ComplexType)
      {
         ComplexType type = (ComplexType) item;
         return new IdentityInequality(type).getInequalities();
      }

      Field field = (Field) item;
      if (!field.isSearchable())
      {
         System.err.println("field " + field + " is not searchable");
         return null;
      }

      if (field.isIndexed())
      {
         ComplexEObject parent = _type.instance();
         EObject eo = field.get(parent);
         return ((Searchable) eo).getInequalities();
      }
      else if (field.isAtomic())
      {
         EObject eo = field.createInstance();
         return ((Searchable) eo).getInequalities();
      }

      // must be a complextype so identity inequality should be
      // the only one that applies
      return new IdentityInequality(field).getInequalities();
   }

   private ActionListener _ineqActionListener = new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            Inequality ineq = (Inequality) _ineqCombo.getSelectedItem();
            if (ineq == null) return;

            JComponent comp = (JComponent) ineq.getValueEditor();
            if (comp instanceof AtomicView)
            {
               AtomicEditor editor = ((AtomicView) comp).getEditor();
               if (editor instanceof StringEditor)
               {
                  ((JTextField) editor).getDocument().addDocumentListener(_docListener);
               }
            }

            removeOldComp();
            _valueSpot.add(comp);
            _oldComp = comp;

            com.u2d.ui.desktop.CloseableJInternalFrame.updateSize(FieldFilter.this);
            comp.requestFocusInWindow();
         }

      };

   JComponent _oldComp = null;
   DocumentListener _docListener = new DocumentTrigger();
      
   class DocumentTrigger implements DocumentListener, ActionListener   
   {
      Timer _timer;
      private static final int THRESHOLD_MS = 300;

      DocumentTrigger()
      {
         _timer = new Timer(THRESHOLD_MS, this);
         _timer.setCoalesce(true);
         _timer.setRepeats(false);
      }
      
      public void insertUpdate(DocumentEvent e) { signalChange(); }
      public void removeUpdate(DocumentEvent e) { signalChange(); }
      public void changedUpdate(DocumentEvent e) { signalChange(); }
      
      private void signalChange()
      {
         _timer.restart();
      }

      public void actionPerformed(ActionEvent e)
      {
         fireChange();
      }
   };

   private void removeOldComp()
   {
      if (_oldComp == null) return;
      if (_oldComp instanceof AtomicView)
      {
         AtomicEditor editor = ((AtomicView) _oldComp).getEditor();
         if (editor instanceof JTextField)
         {
            ((JTextField) editor).getDocument().removeDocumentListener(_docListener);
         }
      }
      _valueSpot.remove(_oldComp);
   }

   public void detach()
   {
      removeOldComp();
      Component c;
      for (int i=0; i<_valueSpot.getComponentCount(); i++)
      {
         c = _valueSpot.getComponent(i);
         if (c instanceof EView)
            ((EView) c).detach();
      }
   }

   public QuerySpecification getSpec()
   {
      java.util.LinkedList list = _fieldCombo.getSelectedPath();
      FieldPath fieldPath = new FieldPath(list);
      Inequality ineq = (Inequality) _ineqCombo.getSelectedItem();
      return new QuerySpecification(fieldPath, ineq, ineq.getValue());
   }

   public SimpleQuery getQuery()
   {
      if (_fieldCombo.getSelectedItem() instanceof ComplexType)
      {
         Inequality ineq = (Inequality) _ineqCombo.getSelectedItem();
         ComplexType concreteType = (ComplexType) ineq.getValue();
         return new SimpleQuery(concreteType);
      }
      else
      {
         return new SimpleQuery(_type, getSpec());
      }
   }

   
   
   private EventListenerList _listeners = new EventListenerList();

   public void addValueChangeListener(ChangeListener listener)
   {
      _listeners.add(ChangeListener.class, listener);
   }
   public void removeValueChangeListener(ChangeListener listener)
   {
      _listeners.remove(ChangeListener.class, listener);
   }

   ChangeEvent changeEvent;
   private void fireChange()
   {
      Object[] listeners = _listeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i]==ChangeListener.class)
         {
            if (changeEvent == null)
               changeEvent = new ChangeEvent(this);
            ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
         }
      }
   }
}