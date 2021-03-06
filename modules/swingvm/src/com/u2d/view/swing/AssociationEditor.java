package com.u2d.view.swing;

import com.u2d.view.ListEView;
import com.u2d.view.ActionNotifier;
import com.u2d.view.CompositeView;
import com.u2d.field.Association;
import com.u2d.field.AssociationField;
import com.u2d.find.*;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.list.CriteriaListEO;
import com.u2d.list.SimpleListEO;
import com.u2d.type.atom.StringEO;
import com.u2d.element.Field;
import com.u2d.ui.IconButton;
import com.u2d.ui.JComboTree;
import com.u2d.ui.KeyPressAdapter;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.model.AbstractListEO;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 29, 2006
 * Time: 5:50:22 PM
 */
public class AssociationEditor extends JPanel implements DocumentListener, ActionNotifier
{
   private JTextField _tf;
   private QuerySpecification _spec;
   private AbstractListEO _leo;
   private JPopupMenu _popup;
   private ListEView _view;
   private JList _list;
   private StringEO _value = new StringEO();
   private Field _searchByField;
   private ComplexType _type;
   private ComplexEObject _selectedItem;
   private Association _association;

   public AssociationEditor(Association association)
   {
      _association = association;
      setLayout(new FlowLayout(FlowLayout.LEADING, 3, 0));
      setOpaque(false);

      _type = association.type();

      if (_type.hasDefaultSearchPath())
      {
         _searchByField = Field.forPath(_type.defaultSearchPath());
      }
      else
      {
         _searchByField = _type.firstFieldOfType(StringEO.class, true);
      }

      _tf = new JTextField(6);
      if (_association.field().displaysize() > 0)
      {
         _tf.setColumns(_association.field().displaysize());
      }
      
      _tf.getDocument().addDocumentListener(this);

      final IconButton iconButton = new IconButton(PICK_ICON, PICK_ROLLOVER);
      iconButton.setFocusable(false);
      final JComboTree fieldPicker = new JComboTree(_type.searchTreeModel(), iconButton);
      fieldPicker.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            FieldPath fp = new FieldPath(fieldPicker.getSelectedPath());
            _searchByField = Field.forPath(fp.getPathString());
            _tf.requestFocusInWindow();
            _tf.selectAll();
         }
      });
      
      _tf.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            itemSelected();
         }
      });
      _tf.addKeyListener(new KeyAdapter()
      {
         public void keyPressed(KeyEvent e)
         {
            if (_popup!=null && _popup.isShowing())
            {
               if (e.getKeyCode() == KeyEvent.VK_DOWN ||
                   e.getKeyCode() == KeyEvent.VK_UP)
               {
                  if (_list != null) _list.dispatchEvent(e);
               }
            }
            else
            {
               if (e.getKeyCode() == KeyEvent.VK_DOWN)
               {
                  updateModel();
               }
               else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
               {
                  cancelEdit();
               }
            }
         }
      });
      
      IconButton showListButton = new IconButton(JComboTree.EXPAND_ICON);
      showListButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e) { updateModel(); }
      });

      add(fieldPicker);
      add(_tf);
      add(showListButton);
   }
   
   public void focus()
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            _tf.requestFocusInWindow();
            _tf.selectAll();
         }
      });
   }

   boolean _ignoreChange = false;
   public void clearValue()
   {
      _ignoreChange = true;
      _tf.setText("");
      _ignoreChange = false;
   }
   public void renderValue(ComplexEObject parent)
   {
      _ignoreChange = true;
      _selectedItem = parent;
      if (_searchByField != null)
      {
         EObject value = _searchByField.get(parent);
         _tf.setText(value.toString());
      }
      _ignoreChange = false;
   }
   public void bindValue(Association association)
   {
      if (_selectedItem != null)
         association.set(_selectedItem);
   }
   public ComplexEObject bind() { return _selectedItem; }

   public void insertUpdate(DocumentEvent e) { updateModel(); }
   public void removeUpdate(DocumentEvent e) { updateModel(); }
   public void changedUpdate(DocumentEvent e) { updateModel(); }

   public void detach()
   {
      if (_view != null) _view.detach();
   }
   
   private void updateModel()
   {
      if (_ignoreChange) return;
      
      _value.setValue(_tf.getText());
      FieldPath fp = new FieldPath(_searchByField.fullPath());

      if (_leo == null)
      {
         AssociationField field = (AssociationField) _association.field();
         if (field.hasListAssociationConstraint() && field.associationOptions(_association.parent()) != null)
         {
            AbstractListEO leo = field.associationOptions(_association.parent());
            _leo = simpleFilter(leo);
         }
         else
         {
            Inequality startsWith = new TextualInequalities(_searchByField).new TextStarts();
            _spec = new QuerySpecification(fp, startsWith, _value);
            CompositeQuery query = new CompositeQuery(_type);
            field.bindConstraintTo(query, _association.parent());
            query.addSpecification(_spec);
         
            _leo = new CriteriaListEO(query);
         }

         _view = _leo.getAssociationView();
         setupListPopupView();
      }
      else
      {
         if (_leo instanceof CriteriaListEO)
         {
            _spec.getFieldPath().setValue(fp);
            _spec.setValue(_value);

            CompositeQuery query = new CompositeQuery(_type);
            AssociationField field = (AssociationField) _association.field();
            field.bindConstraintTo(query, _association.parent());
            query.addSpecification(_spec);
            ((CriteriaListEO) _leo).setQuery(query);
         }
         else
         {
            //??
         }
      }

      _popup.show(_tf, 0, _tf.getSize().height);
      _list.setSelectedIndex(0);
      _tf.requestFocusInWindow();
   }
   
   private AbstractListEO simpleFilter(AbstractListEO leo)
   {
      SimpleListEO filtered = new SimpleListEO();
      for (int i=0; i<leo.getSize(); i++)
      {
         ComplexEObject eo = (ComplexEObject) leo.getElementAt(i);
         String fieldValue = _searchByField.get(eo).toString();
         if (fieldValue.startsWith(_value.stringValue()))
         {
            filtered.add(eo);
         }
      }
      return filtered;
   }

   private void setupListPopupView()
   {
      if (_view instanceof CompositeView) {
         CompositeView pageView = (CompositeView) _view;
         _list = (JList) pageView.getInnerView();
      } else {
         _list = (JList) _view;
      }
      _list.setVisibleRowCount(6);

      _list.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            itemSelected();
         }
      });
      _list.addKeyListener(new KeyPressAdapter(new KeyAdapter()
      {
         public void keyPressed(KeyEvent e)
         {
            itemSelected();
         }
      }, KeyEvent.VK_ENTER));

      _popup = new JPopupMenu();
      _popup.add((JComponent) _view);
   }

   private void itemSelected()
   {
      if (_list == null) return; // pressed enter too quickly
      ComplexEObject selectedItem = (ComplexEObject) _list.getSelectedValue();
      if (selectedItem == null) return;
      _selectedItem = selectedItem;
      renderValue(selectedItem);
      _popup.setVisible(false);
      fireActionPerformed();
   }
   private void cancelEdit()
   {
      if (_popup != null && _popup.isVisible())
         _popup.setVisible(false);
      fireActionPerformed();
   }

   public static ImageIcon PICK_ICON, PICK_ROLLOVER;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/fieldpick.png");
      PICK_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/fieldpick-hover.png");
      PICK_ROLLOVER = new ImageIcon(imgURL);
   }

   private EventListenerList _listeners = new EventListenerList();
   public void addActionListener(ActionListener l)
   {
      _listeners.add(ActionListener.class, l);
   }
   public void removeActionListener(ActionListener l)
   {
      _listeners.remove(ActionListener.class, l);
   }

   ActionEvent aevt = null;
   protected void fireActionPerformed()
   {
      Object[] listeners = _listeners.getListenerList();
       for (int i = listeners.length-2; i>=0; i-=2)
       {
           if (listeners[i]==ActionListener.class)
           {
              if (aevt == null)
              {
                    aevt = new ActionEvent(AssociationEditor.this,
                                           ActionEvent.ACTION_PERFORMED,
                                           "association made",
                                           new Date().getTime(),
                                           0);
              }
              ((ActionListener)listeners[i+1]).actionPerformed(aevt);
           }
       }
   }
   
}
