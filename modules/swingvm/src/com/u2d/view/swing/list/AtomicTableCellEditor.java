package com.u2d.view.swing.list;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.DateEO;
import com.u2d.view.swing.atom.BooleanCheckboxEditor;
import com.u2d.view.swing.atom.TextEditor;
import com.u2d.view.ActionNotifier;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 17, 2006
 * Time: 10:10:33 PM
 */
public class AtomicTableCellEditor extends AbstractCellEditor
                       implements TableCellRenderer, TableCellEditor
{
   Map<Class, AtomicRenderer> renderers = new HashMap<Class, AtomicRenderer>();
   Map<Class, AtomicEditor> editors = new HashMap<Class, AtomicEditor>();
   Map<Class, AtomicEObject> containers = new HashMap<Class, AtomicEObject>();
   AtomicEditor currentEditor;
   Class currentType;

   public AtomicTableCellEditor() { }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
   {
      AtomicEObject aeo = (AtomicEObject) value;
      AtomicRenderer renderer = fetchRenderer(aeo);
      renderer.render(aeo);

      return highlight(table, (JComponent) renderer, isSelected, hasFocus);
   }
   public static Border NOFOCUSBORDER = BorderFactory.createEmptyBorder(1,1,1,1);
   private JComponent highlight(JTable table, JComponent comp, boolean isSelected, boolean hasFocus)
   {
      comp.setForeground( (isSelected) ? table.getSelectionForeground() : table.getForeground());
      comp.setBackground( (isSelected) ? table.getSelectionBackground() : table.getBackground());
      comp.setBorder( (hasFocus) ? UIManager.getBorder("Table.focusCellHighlightBorder") : NOFOCUSBORDER);
      return comp;
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
   {
      AtomicEObject aeo = (AtomicEObject) value;

      currentType = aeo.getClass();
      if (containers.get(currentType) == null)
      {
         containers.put(currentType, (AtomicEObject) aeo.makeCopy());
      }

      currentEditor = fetchEditor(aeo);
      currentEditor.render(aeo);
         
      return (Component) currentEditor;
   }

   private AtomicEditor fetchEditor(AtomicEObject aeo)
   {
      Class type = aeo.getClass();
      AtomicEditor editor = editors.get(type);
      if (editor == null)
      {
         if (aeo instanceof BooleanEO)
         {
            editor = new BooleanCheckboxEditor();
         }
         else if (aeo instanceof TextEO)
         {
            TextEditor textEditor = new TextEditor();
            textEditor.setRows(2);
            editor = textEditor;
         }
         else
         {
            editor = aeo.getEditor();
         }
         JComponent comp = (JComponent) editor;
         comp.setOpaque(true);
         comp.setBackground(UIManager.getColor("Table.background"));
         comp.setForeground(UIManager.getColor("Table.foreground"));
         
         ActionListener stopEditorListener = new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               stopCellEditing();
            }
         };
         if (editor instanceof ActionNotifier)
         {
            ((ActionNotifier) editor).addActionListener(stopEditorListener);
         }
            
         editors.put(type, editor);
      }
      return editor;
   }
   private AtomicRenderer fetchRenderer(AtomicEObject aeo)
   {
      Class type = aeo.getClass();
      AtomicRenderer renderer = renderers.get(type);
      if (renderer == null)
      {
         if (aeo instanceof BooleanEO)
         {
            renderer = new BooleanCheckboxEditor();
         }
         else if (aeo instanceof TextEO)
         {
            TextEditor textEditor = (TextEditor) aeo.getRenderer();
            textEditor.setRows(2);
            renderer = textEditor;
         }
         else
         {
            renderer = aeo.getRenderer();
         }

         JComponent comp = (JComponent) renderer;
         comp.setOpaque(true);
         comp.setBackground(UIManager.getColor("Table.background"));
         comp.setForeground(UIManager.getColor("Table.foreground"));

         renderers.put(type, renderer);
      }
      
      return renderer;
   }

   public Object getCellEditorValue()
   {
      AtomicEObject container = containers.get(currentType);
      currentEditor.bind(container);
      return container;
   }

}
