package com.u2d.ui;

import sun.swing.DefaultLookup;
import sun.swing.UIAction;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.awt.AppContext;

import javax.swing.plaf.ListUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.*;
import javax.swing.text.Position;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.TooManyListenersException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sun.java.swing.SwingUtilities2;

/**
 * Eitan Suez
 * 
 * This class is the UI for GridList.java
 * A painstaking adaptation of JList to support 
 * a layout that is similar to IconLayout.
 * 
 * Ideally I would have liked to see the ability to apply
 * layout management to model components such as JList
 * in an orthogonal fashion.  Alas, today we do not have
 * this in Java.  Layout Managers assume a container
 * with child components.  A JList is not modeled as a 
 * container.  Its "child components" are part of the
 * same widget.
 * 
 * I did this primarily to benefit from the support
 * for keyboard navigation, listselectionmodel, etc..
 * that already exists in JList
 * 
 * Used as the basis for the upcoming GridListView in JMatter,
 * which will replace IconListView.
 */
public class BasicGridListUI
      extends ListUI
{
   protected JList list = null;
   protected CellRendererPane rendererPane;
   private Handler handler = new DragFixHandler();

   protected int updateLayoutStateNeeded = modelChanged;

   private long timeFactor = 1000L;

   protected final static int modelChanged = 1 << 1;
   protected final static int selectionModelChanged = 1 << 2;
   protected final static int fontChanged = 1 << 3;
   protected final static int prototypeCellValueChanged = 1 << 4;
   protected final static int cellRendererChanged = 1 << 5;
   private final static int componentOrientationChanged = 1 << 6;


   static void loadActionMap(LazyActionMap map)
   {
      map.put(new Actions(Actions.SELECT_PREVIOUS_COLUMN));
      map.put(new Actions(Actions.SELECT_PREVIOUS_COLUMN_EXTEND));
      map.put(new Actions(Actions.SELECT_PREVIOUS_COLUMN_CHANGE_LEAD));
      map.put(new Actions(Actions.SELECT_NEXT_COLUMN));
      map.put(new Actions(Actions.SELECT_NEXT_COLUMN_EXTEND));
      map.put(new Actions(Actions.SELECT_NEXT_COLUMN_CHANGE_LEAD));
      map.put(new Actions(Actions.SELECT_PREVIOUS_ROW));
      map.put(new Actions(Actions.SELECT_PREVIOUS_ROW_EXTEND));
      map.put(new Actions(Actions.SELECT_PREVIOUS_ROW_CHANGE_LEAD));
      map.put(new Actions(Actions.SELECT_NEXT_ROW));
      map.put(new Actions(Actions.SELECT_NEXT_ROW_EXTEND));
      map.put(new Actions(Actions.SELECT_NEXT_ROW_CHANGE_LEAD));
      map.put(new Actions(Actions.SELECT_FIRST_ROW));
      map.put(new Actions(Actions.SELECT_FIRST_ROW_EXTEND));
      map.put(new Actions(Actions.SELECT_FIRST_ROW_CHANGE_LEAD));
      map.put(new Actions(Actions.SELECT_LAST_ROW));
      map.put(new Actions(Actions.SELECT_LAST_ROW_EXTEND));
      map.put(new Actions(Actions.SELECT_LAST_ROW_CHANGE_LEAD));
      map.put(new Actions(Actions.SCROLL_UP));
      map.put(new Actions(Actions.SCROLL_UP_EXTEND));
      map.put(new Actions(Actions.SCROLL_UP_CHANGE_LEAD));
      map.put(new Actions(Actions.SCROLL_DOWN));
      map.put(new Actions(Actions.SCROLL_DOWN_EXTEND));
      map.put(new Actions(Actions.SCROLL_DOWN_CHANGE_LEAD));
      map.put(new Actions(Actions.SELECT_ALL));
      map.put(new Actions(Actions.CLEAR_SELECTION));
      map.put(new Actions(Actions.ADD_TO_SELECTION));
      map.put(new Actions(Actions.TOGGLE_AND_ANCHOR));
      map.put(new Actions(Actions.EXTEND_TO));
      map.put(new Actions(Actions.MOVE_SELECTION_TO));

      map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
      map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
      map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
   }

   
   
   
   public void paint(Graphics g, JComponent c)
   {
      maybeUpdateLayoutState();

      ListCellRenderer renderer = list.getCellRenderer();
      ListModel dataModel = list.getModel();
      ListSelectionModel selModel = list.getSelectionModel();
      int size = dataModel.getSize();

      if ((renderer == null) || size == 0)
      {
         return;
      }

      Rectangle paintBounds = g.getClipBounds();

      int leadIndex = list.getLeadSelectionIndex();

      for (int index=0; index<size; index++)
      {
         g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width, paintBounds.height);
         paintCell(g, index, renderer, dataModel, selModel, leadIndex);
      }
      
   }

   protected void paintCell(Graphics g, int index, ListCellRenderer cellRenderer, 
                            ListModel dataModel, ListSelectionModel selModel,
                            int leadIndex)
   {
      Object value = dataModel.getElementAt(index);
      boolean cellHasFocus = list.hasFocus() && (index == leadIndex);
      boolean isSelected = selModel.isSelectedIndex(index);

      Component comp =
            cellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      Rectangle bounds = getCellBounds(index);
      
      int cx = bounds.x;
      int cy = bounds.y;
      int cw = bounds.width;
      int ch = bounds.height;

      rendererPane.paintComponent(g, comp, list, cx, cy, cw, ch, true);
   }



   public Dimension getPreferredSize(JComponent c)
   {
      return _layout.getPreferredSize(list);
   }


   protected void selectPreviousIndex()
   {
      int s = list.getSelectedIndex();
      if (s > 0)
      {
         s -= 1;
         list.setSelectedIndex(s);
         list.ensureIndexIsVisible(s);
      }
   }


   protected void selectNextIndex()
   {
      int s = list.getSelectedIndex();
      if ((s + 1) < list.getModel().getSize())
      {
         s += 1;
         list.setSelectedIndex(s);
         list.ensureIndexIsVisible(s);
      }
   }


   protected void installKeyboardActions()
   {
      InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);

      SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, inputMap);
      
      LazyActionMap.installLazyActionMap(list, BasicGridListUI.class, "List.actionMap");
   }

   InputMap getInputMap(int condition)
   {
      if (condition == JComponent.WHEN_FOCUSED)
      {
         return (InputMap) DefaultLookup.get(list, this, "List.focusInputMap");
      }
      return null;
   }

   protected void uninstallKeyboardActions()
   {
      SwingUtilities.replaceUIActionMap(list, null);
      SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, null);
   }


   protected void installListeners()
   {
      TransferHandler th = list.getTransferHandler();
      if (th == null || th instanceof UIResource)
      {
         list.setTransferHandler(defaultTransferHandler);
      }
      DropTarget dropTarget = list.getDropTarget();
      if (dropTarget instanceof UIResource)
      {
         try
         {
            dropTarget.addDropTargetListener(new ListDropTargetListener());
         }
         catch (TooManyListenersException tmle)
         {
         }
      }

      list.addFocusListener(handler);
      list.addMouseListener(handler);
      list.addMouseMotionListener(handler);
      list.addPropertyChangeListener(handler);
      list.addKeyListener(handler);

      ListModel model = list.getModel();
      if (model != null)
      {
         model.addListDataListener(handler);
      }

      ListSelectionModel selectionModel = list.getSelectionModel();
      if (selectionModel != null)
      {
         selectionModel.addListSelectionListener(handler);
      }
   }


   protected void uninstallListeners()
   {
      list.removeFocusListener(handler);
      list.removeMouseListener(handler);
      list.removeMouseMotionListener(handler);
      list.removePropertyChangeListener(handler);
      list.removeKeyListener(handler);

      ListModel model = list.getModel();
      if (model != null)
      {
         model.removeListDataListener(handler);
      }

      ListSelectionModel selectionModel = list.getSelectionModel();
      if (selectionModel != null)
      {
         selectionModel.removeListSelectionListener(handler);
      }
   }

   GridListLayout _layout = new GridListLayout();

   protected void installDefaults()
   {
      list.setLayout(_layout);

      LookAndFeel.installBorder(list, "List.border");

      LookAndFeel.installColorsAndFont(list, "List.background", "List.foreground", "List.font");

      LookAndFeel.installProperty(list, "opaque", Boolean.TRUE);

      if (list.getCellRenderer() == null)
      {
         list.setCellRenderer((ListCellRenderer) (UIManager.get("List.cellRenderer")));
      }

      Color sbg = list.getSelectionBackground();
      if (sbg == null || sbg instanceof UIResource)
      {
         list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
      }

      Color sfg = list.getSelectionForeground();
      if (sfg == null || sfg instanceof UIResource)
      {
         list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
      }

      Long l = (Long) UIManager.get("List.timeFactor");
      timeFactor = (l != null) ? l.longValue() : 1000L;
   }

   protected void uninstallDefaults()
   {
      LookAndFeel.uninstallBorder(list);
      if (list.getFont() instanceof UIResource)
      {
         list.setFont(null);
      }
      if (list.getForeground() instanceof UIResource)
      {
         list.setForeground(null);
      }
      if (list.getBackground() instanceof UIResource)
      {
         list.setBackground(null);
      }
      if (list.getSelectionBackground() instanceof UIResource)
      {
         list.setSelectionBackground(null);
      }
      if (list.getSelectionForeground() instanceof UIResource)
      {
         list.setSelectionForeground(null);
      }
      if (list.getCellRenderer() instanceof UIResource)
      {
         list.setCellRenderer(null);
      }
      if (list.getTransferHandler() instanceof UIResource)
      {
         list.setTransferHandler(null);
      }
   }


   public void installUI(JComponent c)
   {
      list = (JList) c;

      rendererPane = new CellRendererPane();
      list.add(rendererPane);

      installDefaults();
      installListeners();
      installKeyboardActions();
   }


   public void uninstallUI(JComponent c)
   {
      uninstallListeners();
      uninstallDefaults();
      uninstallKeyboardActions();

      list.remove(rendererPane);
      rendererPane = null;
      list = null;
   }

   public static ComponentUI createUI(JComponent list) { return new BasicGridListUI(); }

   
   public int locationToIndex(JList list, Point location)
   {
      maybeUpdateLayoutState();
      
      int index = _layout.locationToIndex(location);
      if (index >= list.getModel().getSize() || index < 0)
      {
         return -1;
      }
      return index;
   }


   public Point indexToLocation(JList list, int index)
   {
      maybeUpdateLayoutState();
      Rectangle rect = getCellBounds(list, index, index);

      if (rect != null)
      {
         return new Point(rect.x, rect.y);
      }
      return null;
   }


   public Rectangle getCellBounds(JList list, int index1, int index2)
   {
      maybeUpdateLayoutState();

      int minIndex = Math.min(index1, index2);
      int maxIndex = Math.max(index1, index2);

      if (minIndex >= list.getModel().getSize())
      {
         return null;
      }

      Rectangle minBounds = getCellBounds(minIndex);

      if (minBounds == null)
      {
         return null;
      }
      if (minIndex == maxIndex)
      {
         return minBounds;
      }
      Rectangle maxBounds = getCellBounds(maxIndex);

      if (maxBounds != null)
      {
         if (minBounds.x != maxBounds.x)
         {
            minBounds.y = 0;
            minBounds.height = list.getHeight();
         }
         minBounds.add(maxBounds);
      }
      return minBounds;
   }

   private Rectangle getCellBounds(int index)
   {
      maybeUpdateLayoutState();
      return _layout.getCellBounds(index);
   }

   protected void maybeUpdateLayoutState()
   {
      if (updateLayoutStateNeeded != 0)
      {
         _layout.layoutContainer(list);
         updateLayoutStateNeeded = 0;
      }
   }

   static Object getUIOfType(ComponentUI ui, Class klass)
   {
      if (klass.isInstance(ui)) return ui;
      return null;
   }


   private void redrawList()
   {
      list.revalidate();
      list.repaint();
   }


   private static final int CHANGE_LEAD = 0;
   private static final int CHANGE_SELECTION = 1;
   private static final int EXTEND_SELECTION = 2;


   
   
   
   // ====== Inner Classes Begin Here ======
   
   
   private static class Actions
         extends UIAction
   {
      private static final String SELECT_PREVIOUS_COLUMN = "selectPreviousColumn";
      private static final String SELECT_PREVIOUS_COLUMN_EXTEND = "selectPreviousColumnExtendSelection";
      private static final String SELECT_PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
      private static final String SELECT_NEXT_COLUMN = "selectNextColumn";
      private static final String SELECT_NEXT_COLUMN_EXTEND = "selectNextColumnExtendSelection";
      private static final String SELECT_NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
      private static final String SELECT_PREVIOUS_ROW = "selectPreviousRow";
      private static final String SELECT_PREVIOUS_ROW_EXTEND = "selectPreviousRowExtendSelection";
      private static final String SELECT_PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
      private static final String SELECT_NEXT_ROW = "selectNextRow";
      private static final String SELECT_NEXT_ROW_EXTEND = "selectNextRowExtendSelection";
      private static final String SELECT_NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
      private static final String SELECT_FIRST_ROW = "selectFirstRow";
      private static final String SELECT_FIRST_ROW_EXTEND = "selectFirstRowExtendSelection";
      private static final String SELECT_FIRST_ROW_CHANGE_LEAD = "selectFirstRowChangeLead";
      private static final String SELECT_LAST_ROW = "selectLastRow";
      private static final String SELECT_LAST_ROW_EXTEND = "selectLastRowExtendSelection";
      private static final String SELECT_LAST_ROW_CHANGE_LEAD = "selectLastRowChangeLead";
      private static final String SCROLL_UP = "scrollUp";
      private static final String SCROLL_UP_EXTEND = "scrollUpExtendSelection";
      private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
      private static final String SCROLL_DOWN = "scrollDown";
      private static final String SCROLL_DOWN_EXTEND = "scrollDownExtendSelection";
      private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
      private static final String SELECT_ALL = "selectAll";
      private static final String CLEAR_SELECTION = "clearSelection";
      private static final String ADD_TO_SELECTION = "addToSelection";
      private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
      private static final String EXTEND_TO = "extendTo";
      private static final String MOVE_SELECTION_TO = "moveSelectionTo";

      Actions(String name)
      {
         super(name);
      }

      public void actionPerformed(ActionEvent e)
      {
         String name = getName();
         JList list = (JList) e.getSource();
         BasicGridListUI ui = (BasicGridListUI) getUIOfType(
               list.getUI(), BasicGridListUI.class);

         if (name == SELECT_PREVIOUS_COLUMN)
         {
            changeSelection(list, CHANGE_SELECTION, getNextColumnIndex(list, ui, -1), true);
         }
         else if (name == SELECT_PREVIOUS_COLUMN_EXTEND)
         {
            changeSelection(list, EXTEND_SELECTION, getNextColumnIndex(list, ui, -1), true);
         }
         else if (name == SELECT_PREVIOUS_COLUMN_CHANGE_LEAD)
         {
            changeSelection(list, CHANGE_LEAD, getNextColumnIndex(list, ui, -1), true);
         }
         else if (name == SELECT_NEXT_COLUMN)
         {
            changeSelection(list, CHANGE_SELECTION, getNextColumnIndex(list, ui, 1), true);
         }
         else if (name == SELECT_NEXT_COLUMN_EXTEND)
         {
            changeSelection(list, EXTEND_SELECTION, getNextColumnIndex(list, ui, 1), true);
         }
         else if (name == SELECT_NEXT_COLUMN_CHANGE_LEAD)
         {
            changeSelection(list, CHANGE_LEAD, getNextColumnIndex(list, ui, 1), true);
         }
         else if (name == SELECT_PREVIOUS_ROW)
         {
            changeSelection(list, CHANGE_SELECTION, getNextIndex(list, ui, -1), true);
         }
         else if (name == SELECT_PREVIOUS_ROW_EXTEND)
         {
            changeSelection(list, EXTEND_SELECTION, getNextIndex(list, ui, -1), true);
         }
         else if (name == SELECT_PREVIOUS_ROW_CHANGE_LEAD)
         {
            changeSelection(list, CHANGE_LEAD, getNextIndex(list, ui, -1), true);
         }
         else if (name == SELECT_NEXT_ROW)
         {
            changeSelection(list, CHANGE_SELECTION, getNextIndex(list, ui, 1), true);
         }
         else if (name == SELECT_NEXT_ROW_EXTEND)
         {
            changeSelection(list, EXTEND_SELECTION, getNextIndex(list, ui, 1), true);
         }
         else if (name == SELECT_NEXT_ROW_CHANGE_LEAD)
         {
            changeSelection(list, CHANGE_LEAD, getNextIndex(list, ui, 1), true);
         }
         else if (name == SELECT_FIRST_ROW)
         {
            changeSelection(list, CHANGE_SELECTION, 0, true);
         }
         else if (name == SELECT_FIRST_ROW_EXTEND)
         {
            changeSelection(list, EXTEND_SELECTION, 0, true);
         }
         else if (name == SELECT_FIRST_ROW_CHANGE_LEAD)
         {
            changeSelection(list, CHANGE_LEAD, 0, true);
         }
         else if (name == SELECT_LAST_ROW)
         {
            changeSelection(list, CHANGE_SELECTION, list.getModel().getSize() - 1, true);
         }
         else if (name == SELECT_LAST_ROW_EXTEND)
         {
            changeSelection(list, EXTEND_SELECTION, list.getModel().getSize() - 1, true);
         }
         else if (name == SELECT_LAST_ROW_CHANGE_LEAD)
         {
            changeSelection(list, CHANGE_LEAD, list.getModel().getSize() - 1, true);
         }
         else if (name == SCROLL_UP)
         {
            scroll(list, CHANGE_SELECTION, true);
         }
         else if (name == SCROLL_UP_EXTEND)
         {
            scroll(list, EXTEND_SELECTION, true);
         }
         else if (name == SCROLL_UP_CHANGE_LEAD)
         {
            scroll(list, CHANGE_LEAD, true);
         }
         else if (name == SCROLL_DOWN)
         {
            scroll(list, CHANGE_SELECTION, false);
         }
         else if (name == SCROLL_DOWN_EXTEND)
         {
            scroll(list, EXTEND_SELECTION, false);
         }
         else if (name == SCROLL_DOWN_CHANGE_LEAD)
         {
            scroll(list, CHANGE_LEAD, false);
         }
         else if (name == SELECT_ALL)
         {
            selectAll(list);
         }
         else if (name == CLEAR_SELECTION)
         {
            clearSelection(list);
         }
         else if (name == ADD_TO_SELECTION)
         {
            int index = list.getSelectionModel().getLeadSelectionIndex();
            if (!list.isSelectedIndex(index))
            {
               int oldAnchor = list.getSelectionModel().getAnchorSelectionIndex();
               list.setValueIsAdjusting(true);
               list.addSelectionInterval(index, index);
               list.getSelectionModel().setAnchorSelectionIndex(oldAnchor);
               list.setValueIsAdjusting(false);
            }
         }
         else if (name == TOGGLE_AND_ANCHOR)
         {
            int index = list.getSelectionModel().getLeadSelectionIndex();
            if (list.isSelectedIndex(index))
            {
               list.removeSelectionInterval(index, index);
            }
            else
            {
               list.addSelectionInterval(index, index);
            }
         }
         else if (name == EXTEND_TO)
         {
            changeSelection(list, EXTEND_SELECTION,
                            list.getSelectionModel().getLeadSelectionIndex(),
                            false);
         }
         else if (name == MOVE_SELECTION_TO)
         {
            changeSelection(list, CHANGE_SELECTION,
                            list.getSelectionModel().getLeadSelectionIndex(),
                            false);
         }
      }

      public boolean isEnabled(Object c)
      {
         Object name = getName();
         if (name == SELECT_PREVIOUS_COLUMN_CHANGE_LEAD ||
               name == SELECT_NEXT_COLUMN_CHANGE_LEAD ||
               name == SELECT_PREVIOUS_ROW_CHANGE_LEAD ||
               name == SELECT_NEXT_ROW_CHANGE_LEAD ||
               name == SELECT_FIRST_ROW_CHANGE_LEAD ||
               name == SELECT_LAST_ROW_CHANGE_LEAD ||
               name == SCROLL_UP_CHANGE_LEAD ||
               name == SCROLL_DOWN_CHANGE_LEAD)
         {

            return c != null && ((JList) c).getSelectionModel()
                  instanceof DefaultListSelectionModel;
         }

         return true;
      }

      private void clearSelection(JList list)
      {
         list.clearSelection();
      }

      private void selectAll(JList list)
      {
         int size = list.getModel().getSize();
         if (size > 0)
         {
            ListSelectionModel lsm = list.getSelectionModel();
            if (lsm.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION)
            {
               int leadIndex = list.getLeadSelectionIndex();
               if (leadIndex != -1)
               {
                  list.setSelectionInterval(leadIndex, leadIndex);
               }
               else if (list.getMinSelectionIndex() == -1)
               {
                  list.setSelectionInterval(0, 0);
                  list.ensureIndexIsVisible(0);
               }
            }
            else
            {
               list.setValueIsAdjusting(true);

               int anchor = lsm.getAnchorSelectionIndex();
               int lead = lsm.getLeadSelectionIndex();
               list.setSelectionInterval(0, size - 1);

               list.addSelectionInterval(anchor, lead);

               list.setValueIsAdjusting(false);
            }
         }
      }

      private void scroll(JList list, int type,
                          boolean up)
      {
         int index = getNextPageIndex(list, up);

         if (index != -1)
         {
            changeSelection(list, type, index, false);

            Rectangle visRect = list.getVisibleRect();
            Rectangle cellBounds = list.getCellBounds(index, index);

            if (!up)
            {
               cellBounds.y = Math.max(0, cellBounds.y +
                     cellBounds.height - visRect.height);
               cellBounds.height = visRect.height;
            }
            else
            {
               cellBounds.height = visRect.height;
            }
            list.scrollRectToVisible(cellBounds);
         }
      }

      private int getNextPageIndex(JList list, boolean up)
      {
         if (up)
         {
            int index = list.getFirstVisibleIndex();
            ListSelectionModel lsm = list.getSelectionModel();

            if (lsm.getLeadSelectionIndex() == index)
            {
               Rectangle visRect = list.getVisibleRect();
               visRect.y = Math.max(0, visRect.y - visRect.height);
               index = list.locationToIndex(visRect.getLocation());
            }
            return index;
         }
         int index = list.getLastVisibleIndex();
         ListSelectionModel lsm = list.getSelectionModel();

         if (index == -1)
         {
            index = list.getModel().getSize() - 1;
         }
         if (lsm.getLeadSelectionIndex() == index)
         {
            Rectangle visRect = list.getVisibleRect();
            visRect.y += visRect.height + visRect.height - 1;
            index = list.locationToIndex(visRect.getLocation());
            if (index == -1)
            {
               index = list.getModel().getSize() - 1;
            }
         }
         return index;
      }

      private void changeSelection(JList list, int type,
                                   int index, boolean scroll)
      {
         if (index >= 0 && index < list.getModel().getSize())
         {
            ListSelectionModel lsm = list.getSelectionModel();

            if (type == CHANGE_LEAD &&
                  list.getSelectionMode()
                        != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
            {

               type = CHANGE_SELECTION;
            }

            if (type == EXTEND_SELECTION)
            {
               int anchor = lsm.getAnchorSelectionIndex();
               if (anchor == -1)
               {
                  anchor = index;
               }

               list.setSelectionInterval(anchor, index);
            }
            else if (type == CHANGE_SELECTION)
            {
               list.setSelectedIndex(index);
            }
            else
            {
               ((DefaultListSelectionModel) lsm).moveLeadSelectionIndex(index);
            }
            if (scroll)
            {
               list.ensureIndexIsVisible(index);
            }
         }
      }

      private int getNextColumnIndex(JList list, BasicGridListUI ui, int amount)
      {
         int next = ( list.getSelectedIndex() + amount );
         int size = list.getModel().getSize();
         next += size;  // because modulo for negative numbers doesn't work
         next %= size;
         return next;
      }

      private int getNextIndex(JList list, BasicGridListUI ui, int amount)
      {
         return (ui._layout.nextRowIndex(list.getSelectedIndex(), list.getModel().getSize(), amount));
      }
   }


   
   private class Handler
         implements FocusListener, KeyListener,
         ListDataListener, ListSelectionListener,
         MouseInputListener, PropertyChangeListener
   {
      private String prefix = "";
      private String typedString = "";
      private long lastTime = 0L;

      public void keyTyped(KeyEvent e)
      {
         JList src = (JList) e.getSource();
         ListModel model = src.getModel();

         if (model.getSize() == 0 || e.isAltDown() || e.isControlDown() || e.isMetaDown() ||
               isNavigationKey(e))
         {
            return;
         }
         boolean startingFromSelection = true;

         char c = e.getKeyChar();

         long time = e.getWhen();
         int startIndex = src.getLeadSelectionIndex();
         if (time - lastTime < timeFactor)
         {
            typedString += c;
            if ((prefix.length() == 1) && (c == prefix.charAt(0)))
            {
               startIndex++;
            }
            else
            {
               prefix = typedString;
            }
         }
         else
         {
            startIndex++;
            typedString = "" + c;
            prefix = typedString;
         }
         lastTime = time;

         if (startIndex < 0 || startIndex >= model.getSize())
         {
            startingFromSelection = false;
            startIndex = 0;
         }
         int index = src.getNextMatch(prefix, startIndex,
                                      Position.Bias.Forward);
         if (index >= 0)
         {
            src.setSelectedIndex(index);
            src.ensureIndexIsVisible(index);
         }
         else if (startingFromSelection)
         {
            index = src.getNextMatch(prefix, 0,
                                     Position.Bias.Forward);
            if (index >= 0)
            {
               src.setSelectedIndex(index);
               src.ensureIndexIsVisible(index);
            }
         }
      }

      public void keyPressed(KeyEvent e)
      {
         if (isNavigationKey(e))
         {
            prefix = "";
            typedString = "";
            lastTime = 0L;
         }
      }

      public void keyReleased(KeyEvent e) { }

      private boolean isNavigationKey(KeyEvent event)
      {
         InputMap inputMap = list.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         KeyStroke key = KeyStroke.getKeyStrokeForEvent(event);

         if (inputMap != null && inputMap.get(key) != null)
         {
            return true;
         }
         return false;
      }

      public void propertyChange(PropertyChangeEvent e)
      {
         String propertyName = e.getPropertyName();

         if (propertyName == "model")
         {
            ListModel oldModel = (ListModel) e.getOldValue();
            ListModel newModel = (ListModel) e.getNewValue();
            if (oldModel != null)
            {
               oldModel.removeListDataListener(handler);
            }
            if (newModel != null)
            {
               newModel.addListDataListener(handler);
            }
            updateLayoutStateNeeded |= modelChanged;
            redrawList();
         }

         else if (propertyName == "selectionModel")
         {
            ListSelectionModel oldModel = (ListSelectionModel) e.getOldValue();
            ListSelectionModel newModel = (ListSelectionModel) e.getNewValue();
            if (oldModel != null)
            {
               oldModel.removeListSelectionListener(handler);
            }
            if (newModel != null)
            {
               newModel.addListSelectionListener(handler);
            }
            updateLayoutStateNeeded |= modelChanged;
            redrawList();
         }
         else if (propertyName == "cellRenderer")
         {
            updateLayoutStateNeeded |= cellRendererChanged;
            redrawList();
         }
         else if (propertyName == "font")
         {
            updateLayoutStateNeeded |= fontChanged;
            redrawList();
         }
         else if (propertyName == "prototypeCellValue")
         {
            updateLayoutStateNeeded |= prototypeCellValueChanged;
            redrawList();
         }
         else if (propertyName == "cellRenderer")
         {
            updateLayoutStateNeeded |= cellRendererChanged;
            redrawList();
         }
         else if (propertyName == "selectionForeground")
         {
            list.repaint();
         }
         else if (propertyName == "selectionBackground")
         {
            list.repaint();
         }
         else if ("componentOrientation" == propertyName)
         {
            updateLayoutStateNeeded |= componentOrientationChanged;
            redrawList();

            InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
            SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED,
                                             inputMap);
         }
         else if ("transferHandler" == propertyName)
         {
            DropTarget dropTarget = list.getDropTarget();
            if (dropTarget instanceof UIResource)
            {
               try
               {
                  dropTarget.addDropTargetListener(new ListDropTargetListener());
               }
               catch (TooManyListenersException tmle)
               {
               }
            }
         }
      }

      public void intervalAdded(ListDataEvent e)
      {
         updateLayoutStateNeeded = modelChanged;

         int minIndex = Math.min(e.getIndex0(), e.getIndex1());
         int maxIndex = Math.max(e.getIndex0(), e.getIndex1());

         ListSelectionModel sm = list.getSelectionModel();
         if (sm != null)
         {
            sm.insertIndexInterval(minIndex, maxIndex - minIndex + 1, true);
         }

         redrawList();
      }


      public void intervalRemoved(ListDataEvent e)
      {
         updateLayoutStateNeeded = modelChanged;

         ListSelectionModel sm = list.getSelectionModel();
         if (sm != null)
         {
            sm.removeIndexInterval(e.getIndex0(), e.getIndex1());
         }

         redrawList();
      }


      public void contentsChanged(ListDataEvent e)
      {
         updateLayoutStateNeeded = modelChanged;
         redrawList();
      }

      public void valueChanged(ListSelectionEvent e)
      {
         maybeUpdateLayoutState();

         Rectangle bounds = getCellBounds(list, e.getFirstIndex(),
                                          e.getLastIndex());

         if (bounds != null)
         {
            list.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
         }
      }

      private boolean selectedOnPress;

      public void mouseClicked(MouseEvent e) { }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mousePressed(MouseEvent e)
      {
         if (e.isConsumed())
         {
            selectedOnPress = false;
            return;
         }
         selectedOnPress = true;
         adjustFocusAndSelection(e);
      }

      private void adjustFocusAndSelection(MouseEvent e)
      {
         if (!SwingUtilities.isLeftMouseButton(e))
         {
            return;
         }

         if (!list.isEnabled())
         {
            return;
         }

         SwingUtilities2.adjustFocus(list);

         adjustSelection(e);
      }

      protected void adjustSelection(MouseEvent e)
      {
         int row = SwingUtilities2.loc2IndexFileList(list, e.getPoint());
         if (row >= 0)
         {
            int anchorIndex = list.getAnchorSelectionIndex();
            if (e.isControlDown())
            {
               if (e.isShiftDown() && anchorIndex != -1)
               {
                  if (list.isSelectedIndex(anchorIndex))
                  {
                     list.addSelectionInterval(anchorIndex, row);
                  }
                  else
                  {
                     list.removeSelectionInterval(anchorIndex, row);
                     list.addSelectionInterval(row, row);
                     list.getSelectionModel().setAnchorSelectionIndex(anchorIndex);
                  }
               }
               else if (list.isSelectedIndex(row))
               {
                  list.removeSelectionInterval(row, row);
               }
               else
               {
                  list.addSelectionInterval(row, row);
               }
            }
            else if (e.isShiftDown() && (anchorIndex != -1))
            {
               list.setSelectionInterval(anchorIndex, row);
            }
            else
            {
               list.setSelectionInterval(row, row);
            }
         }
      }

      public void mouseDragged(MouseEvent e)
      {
         if (e.isConsumed())
         {
            return;
         }
         if (!SwingUtilities.isLeftMouseButton(e))
         {
            return;
         }
         if (!list.isEnabled())
         {
            return;
         }

         mouseDraggedImpl(e);
      }

      protected void mouseDraggedImpl(MouseEvent e)
      {
         if (e.isShiftDown() || e.isControlDown())
         {
            return;
         }

         int row = locationToIndex(list, e.getPoint());
         if (row != -1)
         {
            Rectangle cellBounds = getCellBounds(list, row, row);
            if (cellBounds != null)
            {
               list.scrollRectToVisible(cellBounds);
               list.setSelectionInterval(row, row);
            }
         }
      }

      public void mouseMoved(MouseEvent e)
      {
      }

      public void mouseReleased(MouseEvent e)
      {
         if (selectedOnPress)
         {
            if (!SwingUtilities.isLeftMouseButton(e))
            {
               return;
            }

            list.setValueIsAdjusting(false);
         }
         else
         {
            adjustFocusAndSelection(e);
         }
      }

      protected void repaintCellFocus()
      {
         int leadIndex = list.getLeadSelectionIndex();
         if (leadIndex != -1)
         {
            Rectangle r = getCellBounds(list, leadIndex, leadIndex);
            if (r != null)
            {
               list.repaint(r.x, r.y, r.width, r.height);
            }
         }
      }

      public void focusGained(FocusEvent e)
      {
         repaintCellFocus();
      }

      public void focusLost(FocusEvent e)
      {
         repaintCellFocus();
      }
   }


   private class DragFixHandler extends Handler implements DragRecognitionSupport.BeforeDrag
   {
      private boolean dragPressDidSelection;

      public void mousePressed(MouseEvent e)
      {
         if (SwingUtilities2.shouldIgnore(e, list))
         {
            return;
         }

         boolean dragEnabled = list.getDragEnabled();
         boolean grabFocus = true;

         if (dragEnabled)
         {
            int row = SwingUtilities2.loc2IndexFileList(list, e.getPoint());
            if (row != -1 && DragRecognitionSupport.mousePressed(e))
            {
               dragPressDidSelection = false;

               if (e.isControlDown())
               {
                  return;
               }
               else if (!e.isShiftDown() && list.isSelectedIndex(row))
               {
                  list.addSelectionInterval(row, row);
                  return;
               }

               grabFocus = false;
               dragPressDidSelection = true;
            }
         }
         else
         {
            list.setValueIsAdjusting(true);
         }

         if (grabFocus)
         {
            SwingUtilities2.adjustFocus(list);
         }

         adjustSelection(e);
      }

      public void dragStarting(MouseEvent me)
      {
         if (me.isControlDown())
         {
            int row = SwingUtilities2.loc2IndexFileList(list, me.getPoint());
            list.addSelectionInterval(row, row);
         }
      }

      public void mouseDragged(MouseEvent e)
      {
         if (SwingUtilities2.shouldIgnore(e, list))
         {
            return;
         }

         if (list.getDragEnabled())
         {
            DragRecognitionSupport.mouseDragged(e, this);
            return;
         }

         mouseDraggedImpl(e);
      }

      public void mouseReleased(MouseEvent e)
      {
         if (SwingUtilities2.shouldIgnore(e, list))
         {
            return;
         }

         if (list.getDragEnabled())
         {
            MouseEvent me = DragRecognitionSupport.mouseReleased(e);
            if (me != null)
            {
               SwingUtilities2.adjustFocus(list);
               if (!dragPressDidSelection)
               {
                  adjustSelection(me);
               }
            }
         }
         else
         {
            list.setValueIsAdjusting(false);
         }
      }
   }


   static class ListDragGestureRecognizer
         extends BasicDragGestureRecognizer
   {

      protected boolean isDragPossible(MouseEvent e)
      {
         if (super.isDragPossible(e))
         {
            JList list = (JList) this.getComponent(e);
            if (list.getDragEnabled())
            {
               int row = SwingUtilities2.loc2IndexFileList(list, e.getPoint());
               if ((row != -1) && list.isSelectedIndex(row))
               {
                  return true;
               }
            }
         }
         return false;
      }
   }

   class ListDropTargetListener
         extends BasicDropTargetListener
   {
      protected void saveComponentState(JComponent comp)
      {
         JList list = (JList) comp;
         selectedIndices = list.getSelectedIndices();
      }

      protected void restoreComponentState(JComponent comp)
      {
         JList list = (JList) comp;
         list.setSelectedIndices(selectedIndices);
      }

      protected void updateInsertionLocation(JComponent comp, Point p)
      {
         JList list = (JList) comp;
         int index = locationToIndex(list, p);
         if (index != -1)
         {
            list.setSelectionInterval(index, index);
         }
      }

      private int[] selectedIndices;
   }

   private static final TransferHandler defaultTransferHandler = new ListTransferHandler();

   static class ListTransferHandler extends TransferHandler implements UIResource
   {
      protected Transferable createTransferable(JComponent c)
      {
         if (c instanceof JList)
         {
            JList list = (JList) c;
            Object[] values = list.getSelectedValues();

            if (values == null || values.length == 0)
            {
               return null;
            }

            StringBuffer plainBuf = new StringBuffer();
            StringBuffer htmlBuf = new StringBuffer();

            htmlBuf.append("<html>\n<body>\n<ul>\n");

            for (int i = 0; i < values.length; i++)
            {
               Object obj = values[i];
               String val = ((obj == null) ? "" : obj.toString());
               plainBuf.append(val + "\n");
               htmlBuf.append("  <li>" + val + "\n");
            }

            plainBuf.deleteCharAt(plainBuf.length() - 1);
            htmlBuf.append("</ul>\n</body>\n</html>");

            return new BasicTransferable(plainBuf.toString(), htmlBuf.toString());
         }

         return null;
      }

      public int getSourceActions(JComponent c)
      {
         return COPY;
      }

   }
}


/*
 ==============================
 When adapting a JList and modifying it in your own package, as I do here, 
 you find out it has dependencies on package private classes.  So I had
 no choice but to make a copy below..
 ==============================
 */

class LazyActionMap
      extends ActionMapUIResource
{
   private transient Object _loader;

   static void installLazyActionMap(JComponent c, Class loaderClass,
                                    String defaultsKey)
   {
      ActionMap map = (ActionMap) UIManager.get(defaultsKey);
      if (map == null)
      {
         map = new LazyActionMap(loaderClass);
         UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
      }
      SwingUtilities.replaceUIActionMap(c, map);
   }

   static ActionMap getActionMap(Class loaderClass,
                                 String defaultsKey)
   {
      ActionMap map = (ActionMap) UIManager.get(defaultsKey);
      if (map == null)
      {
         map = new LazyActionMap(loaderClass);
         UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
      }
      return map;
   }


   private LazyActionMap(Class loader)
   {
      _loader = loader;
   }

   public void put(Action action)
   {
      put(action.getValue(Action.NAME), action);
   }

   public void put(Object key, Action action)
   {
      loadIfNecessary();
      super.put(key, action);
   }

   public Action get(Object key)
   {
      loadIfNecessary();
      return super.get(key);
   }

   public void remove(Object key)
   {
      loadIfNecessary();
      super.remove(key);
   }

   public void clear()
   {
      loadIfNecessary();
      super.clear();
   }

   public Object[] keys()
   {
      loadIfNecessary();
      return super.keys();
   }

   public int size()
   {
      loadIfNecessary();
      return super.size();
   }

   public Object[] allKeys()
   {
      loadIfNecessary();
      return super.allKeys();
   }

   public void setParent(ActionMap map)
   {
      loadIfNecessary();
      super.setParent(map);
   }

   private void loadIfNecessary()
   {
      if (_loader != null)
      {
         Object loader = _loader;

         _loader = null;
         Class klass = (Class) loader;
         try
         {
            Method method = klass.getDeclaredMethod("loadActionMap",
                                                    new Class[]{LazyActionMap.class});
            method.invoke(klass, new Object[]{this});
         }
         catch (NoSuchMethodException nsme)
         {
            assert false : "LazyActionMap unable to load actions " +
                  klass;
         }
         catch (IllegalAccessException iae)
         {
            assert false : "LazyActionMap unable to load actions " +
                  iae;
         }
         catch (InvocationTargetException ite)
         {
            assert false : "LazyActionMap unable to load actions " +
                  ite;
         }
         catch (IllegalArgumentException iae)
         {
            assert false : "LazyActionMap unable to load actions " +
                  iae;
         }
      }
   }
}

// === support class === //

class BasicDragGestureRecognizer
      implements MouseListener, MouseMotionListener
{

   private MouseEvent dndArmedEvent = null;

   private static int getMotionThreshold()
   {
      return DragSource.getDragThreshold();
   }

   protected int mapDragOperationFromModifiers(MouseEvent e)
   {
      int mods = e.getModifiersEx();

      if ((mods & InputEvent.BUTTON1_DOWN_MASK) != InputEvent.BUTTON1_DOWN_MASK)
      {
         return TransferHandler.NONE;
      }

      JComponent c = getComponent(e);
      TransferHandler th = c.getTransferHandler();
      return SunDragSourceContextPeer.convertModifiersToDropAction(mods, th.getSourceActions(c));
   }

   public void mouseClicked(MouseEvent e)
   {
   }

   public void mousePressed(MouseEvent e)
   {
      dndArmedEvent = null;

      if (isDragPossible(e) && mapDragOperationFromModifiers(e) != TransferHandler.NONE)
      {
         dndArmedEvent = e;
         e.consume();
      }
   }

   public void mouseReleased(MouseEvent e)
   {
      dndArmedEvent = null;
   }

   public void mouseEntered(MouseEvent e)
   {
      //dndArmedEvent = null;
   }

   public void mouseExited(MouseEvent e)
   {
      //if (dndArmedEvent != null && mapDragOperationFromModifiers(e) == TransferHandler.NONE) {
      //    dndArmedEvent = null;
      //}
   }

   public void mouseDragged(MouseEvent e)
   {
      if (dndArmedEvent != null)
      {
         e.consume();

         int action = mapDragOperationFromModifiers(e);

         if (action == TransferHandler.NONE)
         {
            return;
         }

         int dx = Math.abs(e.getX() - dndArmedEvent.getX());
         int dy = Math.abs(e.getY() - dndArmedEvent.getY());
         if ((dx > getMotionThreshold()) || (dy > getMotionThreshold()))
         {
            // start transfer... shouldn't be a click at this point
            JComponent c = getComponent(e);
            TransferHandler th = c.getTransferHandler();
            th.exportAsDrag(c, dndArmedEvent, action);
            dndArmedEvent = null;
         }
      }
   }

   public void mouseMoved(MouseEvent e)
   {
   }

   private TransferHandler getTransferHandler(MouseEvent e)
   {
      JComponent c = getComponent(e);
      return c == null ? null : c.getTransferHandler();
   }

   protected boolean isDragPossible(MouseEvent e)
   {
      JComponent c = getComponent(e);
      return (c == null) ? true : (c.getTransferHandler() != null);
   }

   protected JComponent getComponent(MouseEvent e)
   {
      Object src = e.getSource();
      if (src instanceof JComponent)
      {
         JComponent c = (JComponent) src;
         return c;
      }
      return null;
   }

}

// === support class === //

class BasicDropTargetListener
      implements DropTargetListener, UIResource, ActionListener
{

   protected BasicDropTargetListener()
   {
   }


   protected void saveComponentState(JComponent c)
   {
   }

   protected void restoreComponentState(JComponent c)
   {
   }

   protected void restoreComponentStateForDrop(JComponent c)
   {
   }

   protected void updateInsertionLocation(JComponent c, Point p)
   {
   }

   private static final int AUTOSCROLL_INSET = 10;

   void updateAutoscrollRegion(JComponent c)
   {
      // compute the outer
      Rectangle visible = c.getVisibleRect();
      outer.reshape(visible.x, visible.y, visible.width, visible.height);

      // compute the insets
      Insets i = new Insets(0, 0, 0, 0);
      if (c instanceof Scrollable)
      {
         int minSize = 2 * AUTOSCROLL_INSET;

         if (visible.width >= minSize)
         {
            i.left = i.right = AUTOSCROLL_INSET;
         }

         if (visible.height >= minSize)
         {
            i.top = i.bottom = AUTOSCROLL_INSET;
         }
      }

      // set the inner from the insets
      inner.reshape(visible.x + i.left,
                    visible.y + i.top,
                    visible.width - (i.left + i.right),
                    visible.height - (i.top + i.bottom));
   }

   void autoscroll(JComponent c, Point pos)
   {
      if (c instanceof Scrollable)
      {
         Scrollable s = (Scrollable) c;
         if (pos.y < inner.y)
         {
            // scroll upward
            int dy = s.getScrollableUnitIncrement(outer, SwingConstants.VERTICAL, -1);
            Rectangle r = new Rectangle(inner.x, outer.y - dy, inner.width, dy);
            c.scrollRectToVisible(r);
         }
         else if (pos.y > (inner.y + inner.height))
         {
            // scroll downard
            int dy = s.getScrollableUnitIncrement(outer, SwingConstants.VERTICAL, 1);
            Rectangle r = new Rectangle(inner.x, outer.y + outer.height, inner.width, dy);
            c.scrollRectToVisible(r);
         }

         if (pos.x < inner.x)
         {
            // scroll left
            int dx = s.getScrollableUnitIncrement(outer, SwingConstants.HORIZONTAL, -1);
            Rectangle r = new Rectangle(outer.x - dx, inner.y, dx, inner.height);
            c.scrollRectToVisible(r);
         }
         else if (pos.x > (inner.x + inner.width))
         {
            // scroll right
            int dx = s.getScrollableUnitIncrement(outer, SwingConstants.HORIZONTAL, 1);
            Rectangle r = new Rectangle(outer.x + outer.width, inner.y, dx, inner.height);
            c.scrollRectToVisible(r);
         }
      }
   }

   private void initPropertiesIfNecessary()
   {
      if (timer == null)
      {
         Toolkit t = Toolkit.getDefaultToolkit();
         Integer initial = new Integer(100);
         Integer interval = new Integer(100);

         try
         {
            initial = (Integer) t.getDesktopProperty(
                  "DnD.Autoscroll.initialDelay");
         }
         catch (Exception e)
         {
            // ignore
         }
         try
         {
            interval = (Integer) t.getDesktopProperty(
                  "DnD.Autoscroll.interval");
         }
         catch (Exception e)
         {
            // ignore
         }
         timer = new Timer(interval.intValue(), this);

         timer.setCoalesce(true);
         timer.setInitialDelay(initial.intValue());

         try
         {
            hysteresis = ((Integer) t.getDesktopProperty(
                  "DnD.Autoscroll.cursorHysteresis")).intValue();
         }
         catch (Exception e)
         {
            // ignore
         }
      }
   }

   static JComponent getComponent(DropTargetEvent e)
   {
      DropTargetContext context = e.getDropTargetContext();
      return (JComponent) context.getComponent();
   }

   // --- ActionListener methods --------------------------------------

   public synchronized void actionPerformed(ActionEvent e)
   {
      updateAutoscrollRegion(component);
      if (outer.contains(lastPosition) && !inner.contains(lastPosition))
      {
         autoscroll(component, lastPosition);
      }
   }

   // --- DropTargetListener methods -----------------------------------

   public void dragEnter(DropTargetDragEvent e)
   {
      component = getComponent(e);
      TransferHandler th = component.getTransferHandler();
      canImport = th.canImport(component, e.getCurrentDataFlavors());
      if (canImport)
      {
         saveComponentState(component);
         lastPosition = e.getLocation();
         updateAutoscrollRegion(component);
         initPropertiesIfNecessary();
      }
   }

   public void dragOver(DropTargetDragEvent e)
   {
      if (canImport)
      {
         Point p = e.getLocation();
         updateInsertionLocation(component, p);

         // check autoscroll
         synchronized (this)
         {
            if (Math.abs(p.x - lastPosition.x) > hysteresis ||
                  Math.abs(p.y - lastPosition.y) > hysteresis)
            {
               // no autoscroll 
               if (timer.isRunning()) timer.stop();
            }
            else
            {
               if (!timer.isRunning()) timer.start();
            }
            lastPosition = p;
         }
      }
   }

   public void dragExit(DropTargetEvent e)
   {
      if (canImport)
      {
         restoreComponentState(component);
      }
      cleanup();
   }

   public void drop(DropTargetDropEvent e)
   {
      if (canImport)
      {
         restoreComponentStateForDrop(component);
      }
      cleanup();
   }

   public void dropActionChanged(DropTargetDragEvent e)
   {
   }

   private void cleanup()
   {
      if (timer != null)
      {
         timer.stop();
      }
      component = null;
      lastPosition = null;
   }

   // --- fields --------------------------------------------------

   private Timer timer;
   private Point lastPosition;
   private Rectangle outer = new Rectangle();
   private Rectangle inner = new Rectangle();
   private int hysteresis = 10;
   private boolean canImport;

   private JComponent component;

}




// === support class === //

class DragRecognitionSupport
{
   private int motionThreshold;
   private MouseEvent dndArmedEvent;
   private JComponent component;

   public static interface BeforeDrag
   {
      public void dragStarting(MouseEvent me);
   }

   private static DragRecognitionSupport getDragRecognitionSupport()
   {
      DragRecognitionSupport support =
            (DragRecognitionSupport) AppContext.getAppContext().
                  get(DragRecognitionSupport.class);

      if (support == null)
      {
         support = new DragRecognitionSupport();
         AppContext.getAppContext().put(DragRecognitionSupport.class, support);
      }

      return support;
   }

   public static boolean mousePressed(MouseEvent me)
   {
      return ((DragRecognitionSupport) getDragRecognitionSupport()).
            mousePressedImpl(me);
   }

   public static MouseEvent mouseReleased(MouseEvent me)
   {
      return ((DragRecognitionSupport) getDragRecognitionSupport()).
            mouseReleasedImpl(me);
   }

   public static boolean mouseDragged(MouseEvent me, BeforeDrag bd)
   {
      return ((DragRecognitionSupport) getDragRecognitionSupport()).
            mouseDraggedImpl(me, bd);
   }

   private void clearState()
   {
      dndArmedEvent = null;
      component = null;
   }

   private int mapDragOperationFromModifiers(MouseEvent me,
                                             TransferHandler th)
   {

      if (th == null || !SwingUtilities.isLeftMouseButton(me))
      {
         return TransferHandler.NONE;
      }

      return SunDragSourceContextPeer.
            convertModifiersToDropAction(me.getModifiersEx(),
                                         th.getSourceActions(component));
   }

   private boolean mousePressedImpl(MouseEvent me)
   {
      component = (JComponent) me.getSource();

      if (mapDragOperationFromModifiers(me, component.getTransferHandler())
            != TransferHandler.NONE)
      {

         motionThreshold = DragSource.getDragThreshold();
         dndArmedEvent = me;
         return true;
      }

      clearState();
      return false;
   }

   private MouseEvent mouseReleasedImpl(MouseEvent me)
   {
      if (dndArmedEvent == null)
      {
         return null;
      }

      MouseEvent retEvent = null;

      if (me.getSource() == component)
      {
         retEvent = dndArmedEvent;
      } // else component has changed unexpectedly, so return null

      clearState();
      return retEvent;
   }

   private boolean mouseDraggedImpl(MouseEvent me, BeforeDrag bd)
   {
      if (dndArmedEvent == null)
      {
         return false;
      }

      if (me.getSource() != component)
      {
         clearState();
         return false;
      }

      int dx = Math.abs(me.getX() - dndArmedEvent.getX());
      int dy = Math.abs(me.getY() - dndArmedEvent.getY());
      if ((dx > motionThreshold) || (dy > motionThreshold))
      {
         TransferHandler th = component.getTransferHandler();
         int action = mapDragOperationFromModifiers(me, th);
         if (action != TransferHandler.NONE)
         {
            if (bd != null)
            {
               bd.dragStarting(dndArmedEvent);
            }
            th.exportAsDrag(component, dndArmedEvent, action);
            clearState();
         }
      }

      return true;
   }
}

// === support class === //


class BasicTransferable
      implements Transferable, UIResource
{

   protected String plainData;
   protected String htmlData;

   private static DataFlavor[] htmlFlavors;
   private static DataFlavor[] stringFlavors;
   private static DataFlavor[] plainFlavors;

   static
   {
      try
      {
         htmlFlavors = new DataFlavor[3];
         htmlFlavors[0] = new DataFlavor("text/html;class=java.lang.String");
         htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader");
         htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");

         plainFlavors = new DataFlavor[3];
         plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String");
         plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
         plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");

         stringFlavors = new DataFlavor[2];
         stringFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.String");
         stringFlavors[1] = DataFlavor.stringFlavor;

      }
      catch (ClassNotFoundException cle)
      {
         System.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
      }
   }

   public BasicTransferable(String plainData, String htmlData)
   {
      this.plainData = plainData;
      this.htmlData = htmlData;
   }


   public DataFlavor[] getTransferDataFlavors()
   {
      DataFlavor[] richerFlavors = getRicherFlavors();
      int nRicher = (richerFlavors != null) ? richerFlavors.length : 0;
      int nHTML = (isHTMLSupported()) ? htmlFlavors.length : 0;
      int nPlain = (isPlainSupported()) ? plainFlavors.length : 0;
      int nString = (isPlainSupported()) ? stringFlavors.length : 0;
      int nFlavors = nRicher + nHTML + nPlain + nString;
      DataFlavor[] flavors = new DataFlavor[nFlavors];

      // fill in the array
      int nDone = 0;
      if (nRicher > 0)
      {
         System.arraycopy(richerFlavors, 0, flavors, nDone, nRicher);
         nDone += nRicher;
      }
      if (nHTML > 0)
      {
         System.arraycopy(htmlFlavors, 0, flavors, nDone, nHTML);
         nDone += nHTML;
      }
      if (nPlain > 0)
      {
         System.arraycopy(plainFlavors, 0, flavors, nDone, nPlain);
         nDone += nPlain;
      }
      if (nString > 0)
      {
         System.arraycopy(stringFlavors, 0, flavors, nDone, nString);
         nDone += nString;
      }
      return flavors;
   }

   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      DataFlavor[] flavors = getTransferDataFlavors();
      for (int i = 0; i < flavors.length; i++)
      {
         if (flavors[i].equals(flavor))
         {
            return true;
         }
      }
      return false;
   }

   public Object getTransferData(DataFlavor flavor)
         throws UnsupportedFlavorException, IOException
   {
      DataFlavor[] richerFlavors = getRicherFlavors();
      if (isRicherFlavor(flavor))
      {
         return getRicherData(flavor);
      }
      else if (isHTMLFlavor(flavor))
      {
         String data = getHTMLData();
         data = (data == null) ? "" : data;
         if (String.class.equals(flavor.getRepresentationClass()))
         {
            return data;
         }
         else if (Reader.class.equals(flavor.getRepresentationClass()))
         {
            return new StringReader(data);
         }
         else if (InputStream.class.equals(flavor.getRepresentationClass()))
         {
            return new StringBufferInputStream(data);
         }
         // fall through to unsupported
      }
      else if (isPlainFlavor(flavor))
      {
         String data = getPlainData();
         data = (data == null) ? "" : data;
         if (String.class.equals(flavor.getRepresentationClass()))
         {
            return data;
         }
         else if (Reader.class.equals(flavor.getRepresentationClass()))
         {
            return new StringReader(data);
         }
         else if (InputStream.class.equals(flavor.getRepresentationClass()))
         {
            return new StringBufferInputStream(data);
         }
         // fall through to unsupported

      }
      else if (isStringFlavor(flavor))
      {
         String data = getPlainData();
         data = (data == null) ? "" : data;
         return data;
      }
      throw new UnsupportedFlavorException(flavor);
   }

   // --- richer subclass flavors ----------------------------------------------

   protected boolean isRicherFlavor(DataFlavor flavor)
   {
      DataFlavor[] richerFlavors = getRicherFlavors();
      int nFlavors = (richerFlavors != null) ? richerFlavors.length : 0;
      for (int i = 0; i < nFlavors; i++)
      {
         if (richerFlavors[i].equals(flavor))
         {
            return true;
         }
      }
      return false;
   }

   protected DataFlavor[] getRicherFlavors()
   {
      return null;
   }

   protected Object getRicherData(DataFlavor flavor)
         throws UnsupportedFlavorException
   {
      return null;
   }

   // --- html flavors ----------------------------------------------------------

   protected boolean isHTMLFlavor(DataFlavor flavor)
   {
      DataFlavor[] flavors = htmlFlavors;
      for (int i = 0; i < flavors.length; i++)
      {
         if (flavors[i].equals(flavor))
         {
            return true;
         }
      }
      return false;
   }

   protected boolean isHTMLSupported()
   {
      return htmlData != null;
   }

   protected String getHTMLData()
   {
      return htmlData;
   }

   // --- plain text flavors ----------------------------------------------------

   protected boolean isPlainFlavor(DataFlavor flavor)
   {
      DataFlavor[] flavors = plainFlavors;
      for (int i = 0; i < flavors.length; i++)
      {
         if (flavors[i].equals(flavor))
         {
            return true;
         }
      }
      return false;
   }

   protected boolean isPlainSupported()
   {
      return plainData != null;
   }

   protected String getPlainData()
   {
      return plainData;
   }

   // --- string flavorss --------------------------------------------------------

   protected boolean isStringFlavor(DataFlavor flavor)
   {
      DataFlavor[] flavors = stringFlavors;
      for (int i = 0; i < flavors.length; i++)
      {
         if (flavors[i].equals(flavor))
         {
            return true;
         }
      }
      return false;
   }


}
