/*
 * Created on May 13, 2004
 */
package com.u2d.view.swing.list;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.model.*;
import com.u2d.ui.sorttable.TableSortSupport;
import com.u2d.ui.sorttable.SortTableModel;
import com.u2d.field.Association;

/**
 * @author Eitan Suez
 */
public class TableView extends ScrollableTable implements ListEView, Selectable, ChangeListener
{
   private AbstractListEO _leo;
   private Map _components = new HashMap();
   private ChangeListener _stopCellEditingEr;

   public TableView(AbstractListEO leo)
   {
      this(leo, leo.tableModel());
   }

   public TableView(AbstractListEO leo, TableModel tableModel)
   {
      _leo = leo;
      setItUp(tableModel);
      _leo.addChangeListener(this);
   }


   private void setItUp(TableModel tableModel)
   {
      setModel(tableModel);

      AtomicTableCellEditor atomRender = new AtomicTableCellEditor();
      setDefaultRenderer(AtomicEObject.class,  atomRender);
      setDefaultRenderer(AbstractAtomicEO.class,  atomRender);

      AtomicTableCellEditor atomEdit = new AtomicTableCellEditor();
      setDefaultEditor(AtomicEObject.class,  atomEdit);
      setDefaultEditor(AbstractAtomicEO.class,  atomEdit);

      AssociationRenderer assocRenderer = new AssociationRenderer();
      setDefaultRenderer(Association.class, assocRenderer);
      AssociationTableCellEditor assocEditor = new AssociationTableCellEditor();
      setDefaultEditor(Association.class,  assocEditor);

      CEOCellRenderer ceoRenderer = new CEOCellRenderer();
      setDefaultRenderer(ComplexEObject.class, ceoRenderer);
      setDefaultRenderer(AbstractComplexEObject.class, ceoRenderer);

      adjustColumnWidths();
      adjustRowHeight();

      if (tableModel instanceof SortTableModel)
      {
         new TableSortSupport(this);
      }

      setRowSelectionAllowed(true);
      setColumnSelectionAllowed(false);
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      getTableHeader().setReorderingAllowed(true);

      _stopCellEditingEr = new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               TableCellEditor editor = getCellEditor();
               if (editor != null)
               {
                  editor.stopCellEditing();
               }
            }
         };
      if (_leo.parentObject() != null)
      {
         _leo.parentObject().addChangeListener(_stopCellEditingEr);
      }
   }

   private void adjustRowHeight()
   {
      TableModel tableModel = getModel();
      if (tableModel.getRowCount() > 0)
      {
         int row = 0;
         int height = getRowHeight(row);
         for (int col=1; col<tableModel.getColumnCount(); col++)
         {
            Component comp = getTableComponentAt(row, col, tableModel);
            int preferredHeight = comp.getPreferredSize().height;
            height = Math.max(height, preferredHeight);
         }
         setRowHeight(height);
      }
   }

   public void stateChanged(ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            tableChanged(new TableModelEvent(getModel(), 0, getModel().getRowCount()-1, 0, TableModelEvent.UPDATE));
         }
      });
   }

   public void contentsChanged(ListDataEvent evt) {}
   public void intervalAdded(ListDataEvent evt) {}
   public void intervalRemoved(ListDataEvent evt) {}

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      if (_leo.parentObject() != null)
      {
         _leo.parentObject().removeChangeListener(_stopCellEditingEr);
      }
      _leo.removeListDataListener(this);
      _leo.removeChangeListener(this);
      for (Iterator itr = _components.values().iterator(); itr.hasNext(); )
      {
         ((EView) itr.next()).detach();
      }
   }


   public ComplexEObject selectedEO()
   {
      int rowIndex = getSelectedRow();
      return (ComplexEObject) getModel().getValueAt(rowIndex, 0);
   }


   class CEOCellRenderer implements TableCellRenderer
   {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column)
      {
         JComponent comp = (JComponent) _components.get(value);

         if (comp == null)
         {
            ComplexEObject ceo = (ComplexEObject) value;
            comp = (JComponent) SwingViewMechanism.getInstance().getListItemViewAdapter(ceo);
            comp.setOpaque(true);
            _components.put(value, comp);
         }

         return highlight(table, comp, isSelected, hasFocus);
      }
   }
   class AssociationRenderer implements TableCellRenderer
   {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column)
      {
         JComponent comp = (JComponent) _components.get(value);

         if (comp == null)
         {
            Association a = (Association) value;
            ComplexEObject ceo = a.get();
            comp = (JComponent) SwingViewMechanism.getInstance().getListItemViewAdapter(ceo);
            comp.setOpaque(true);
            _components.put(value, comp);
         }

         return highlight(table, comp, isSelected, hasFocus);
      }
   }

   public static Border NOFOCUSBORDER = BorderFactory.createEmptyBorder(1,1,1,1);
   private JComponent highlight(JTable table, JComponent comp, boolean isSelected, boolean hasFocus)
   {
      comp.setForeground( (isSelected) ? table.getSelectionForeground() : table.getForeground());
      comp.setBackground( (isSelected) ? table.getSelectionBackground() : table.getBackground());
      comp.setBorder( (hasFocus) ? UIManager.getBorder("Table.focusCellHighlightBorder") : NOFOCUSBORDER);
      return comp;
   }

   public boolean isMinimized() { return false; }

}
