package com.u2d.view.swing;

import com.u2d.view.ListEView;
import com.u2d.view.ActionNotifier;
import com.u2d.view.swing.list.PaginableView;
import com.u2d.field.Association;
import com.u2d.find.QuerySpecification;
import com.u2d.find.FieldPath;
import com.u2d.find.Inequality;
import com.u2d.find.SimpleQuery;
import com.u2d.find.inequalities.TextualInequalities;
import com.u2d.list.CriteriaListEO;
import com.u2d.type.atom.StringEO;
import com.u2d.element.Field;
import com.u2d.ui.IconButton;
import com.u2d.ui.JComboTree;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
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
   private CriteriaListEO _leo;
   private JPopupMenu _popup;
   private ListEView _view;
   private JList _list;
   private StringEO _value = new StringEO();
   private Field _searchByField;
   private ComplexType _type;
   private ComplexEObject _selectedItem;

   public AssociationEditor(Field field)
   {
      setLayout(new FlowLayout(FlowLayout.LEFT));
      setOpaque(false);

      _type = field.fieldtype();

      if (_type.hasDefaultSearchPath())
      {
         _searchByField = Field.forPath(_type.defaultSearchPath());
      }
      else
      {
         _searchByField = _type.firstFieldOfType(StringEO.class, true);
      }

      _tf = new JTextField(6);
      if (field.displaysize() > 0)
      {
         _tf.setColumns(field.displaysize());
      }
      
      _tf.getDocument().addDocumentListener(this);

      final IconButton iconButton = new IconButton(PICK_ICON, PICK_ROLLOVER);
      final JComboTree fieldPicker = new JComboTree(_type.searchTreeModel(), iconButton);
      fieldPicker.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            FieldPath fp = new FieldPath(fieldPicker.getSelectedPath());
            _searchByField = Field.forPath(fp.getPathString());
            _tf.requestFocus();
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
            if (e.getKeyCode() == KeyEvent.VK_DOWN ||
                e.getKeyCode() == KeyEvent.VK_UP)
            {
               _list.dispatchEvent(e);
            }
            else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
               cancelEdit();
            }
         }
      });

      add(fieldPicker);
      add(_tf);
      
//      _tf.addFocusListener(new FocusAdapter()
//      {
//         public void focusLost(FocusEvent e)
//         {
//            boolean innerFocus = SwingUtilities.isDescendingFrom(e.getOppositeComponent(), AssociationEditor.this);
//            if (!innerFocus)
//            {
//               cancelEdit();
//            }
//         }
//      });
      
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _tf.requestFocus();
            _tf.selectAll();
         }
      });
   }

   boolean _closeYourEyes = false;
   public void clearValue()
   {
      _closeYourEyes = true;
      _tf.setText("");
      _closeYourEyes = false;
   }
   public void renderValue(ComplexEObject parent)
   {
      _closeYourEyes = true;
      _selectedItem = parent;
      if (_searchByField != null)
      {
         EObject value = _searchByField.get(parent);
         _tf.setText(value.toString());
      }
      _closeYourEyes = false;
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
      if (_closeYourEyes) return;
      
      _value.setValue(_tf.getText());
      FieldPath fp = new FieldPath(_searchByField.fullPath());

      if (_leo == null)
      {
         Inequality startsWith = new TextualInequalities(_searchByField).new TextStarts();
         _spec = new QuerySpecification(fp, startsWith, _value);
         SimpleQuery _query = new SimpleQuery(_type, _spec);
         _leo = new CriteriaListEO(_query);
         _view = _leo.getAssociationView();

         PaginableView pageView = (PaginableView) _view;
         _list = (JList) (pageView).getInnerView();
         _list.setVisibleRowCount(6);
         
         _list.addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent e)
            {
               itemSelected();
            }
         });
         _list.addKeyListener(new KeyAdapter()
         {
            public void keyPressed(KeyEvent e)
            {
               if (e.getKeyCode() == KeyEvent.VK_ENTER)
               {
                  itemSelected();
               }
            }
         });

         _popup = new JPopupMenu();
         _popup.add((JComponent) _view);
      }
      else
      {
         _spec.getFieldPath().setValue(fp);
         _spec.setValue(_value);
         SimpleQuery _query = new SimpleQuery(_type, _spec);
         _leo.setQuery(_query);
      }

      _popup.show(_tf, 0, _tf.getSize().height);
      _list.setSelectedIndex(0);
      _tf.requestFocus();
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
      ClassLoader loader = AssociationView2.class.getClassLoader();
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
   
   Insets _insets = new Insets(0, 0, 0, 0);
   public Insets getInsets() { return _insets; }


}
