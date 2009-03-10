package com.u2d.ui.multipick;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.HashSet;
import java.util.Arrays;
import java.net.URL;
import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Nov 20, 2008
 * Time: 11:04:38 AM
 */
public class MultiListPicker extends JPanel
{
   protected JLabel label;
   protected JButton pickBtn;

   protected String[] options;
   private JList optionsList;

   private JPopupMenu popup;
   private JCheckBox[] cbs;
   private ChangeListener cbListener;
   private MyListModel myListModel;

   static Icon downIcon;

   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL url = loader.getResource("com/u2d/ui/multipick/arrow_down.png");
      downIcon = new ImageIcon(url);
   }

   public MultiListPicker(String[] options)
   {
      this.options = options;

      MigLayout layout = new MigLayout("fill, insets 0");
      setLayout(layout);
      setOpaque(true);
      setBackground(Color.white);
      
      label = new JLabel();
      label.setOpaque(true);
      label.setMaximumSize(new Dimension(120, 20));
      label.setBackground(Color.white);
      add(label, "growx 1, alignx leading");

      // TODO:  pickBtn should toggle from 'drop down' to 'close' icon based on state
      pickBtn = new JButton(downIcon);
      pickBtn.setMargin(new Insets(0, 0, 0, 0));
      add(pickBtn, "alignx trailing");

      pickBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            int x = label.getLocation().x;
            int y = label.getLocation().y + label.getHeight();

            popup.show(MultiListPicker.this, x, y);
         }
      });

      popup = new JPopupMenu();
      initOptionsList();
      popup.add(optionsList);

      cbListener = new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            int index = optionsList.getSelectedIndex();
            String item = (String) optionsList.getModel().getElementAt(index);
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               values.add(item);
            }
            else
            {
               values.remove(item);
            }
            label.setText(fromCollection());
            myListModel.changed(index); // triggers a repaint
         }
      };
   }

   java.util.Set<String> values = new HashSet<String>();
   public String getValues()
   {
      return fromCollection();
   }

   public String fromCollection()
   {
      String text = "";
      int i=0;
      for (String value : this.values)
      {
         if (i>0)
         {
            text += ",";
         }
         text+=value;
         i++;
      }
      return text;
   }

   public HashSet<String> asCollection(String valuesCommaSeparated)
   {
      String[] values = valuesCommaSeparated.split(",");
      HashSet<String> set = new HashSet<String>(Arrays.asList(values));
      set.remove("");
      return set;
   }

   public void setValues(String valuesCommaSeparated)
   {
      this.values = asCollection(valuesCommaSeparated);
      label.setText(valuesCommaSeparated);

      for (int i=0; i<cbs.length; i++)
      {
         final JCheckBox cb = cbs[i];
         cb.removeChangeListener(cbListener);
         boolean containsIt = values.contains(cb.getText());
         cb.setSelected(containsIt);
         cb.addChangeListener(cbListener);
      }
   }

   private void initOptionsList()
   {
      cbs = new JCheckBox[options.length];
      for (int i=0; i<cbs.length; i++)
      {
         cbs[i] = new JCheckBox();
         cbs[i].setText(options[i]);
         final JCheckBox checkbox = cbs[i];
         cbs[i].addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent e)
            {
               checkbox.setSelected(!checkbox.isSelected());
            }
         });
      }

      optionsList = new ListClickPropagater();
      myListModel = new MyListModel();

      optionsList.setModel(myListModel);
      optionsList.setCellRenderer(new ListCellRenderer()
      {
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
         {
            JCheckBox cb = cbs[index];
            cb.setBackground(isSelected ? list.getSelectionBackground() : Color.white);

            Border border = null;
            if (cellHasFocus) {
                if (isSelected) {
                    border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
                }
                if (border == null) {
                    border = UIManager.getBorder("List.focusCellHighlightBorder");
                }
            } else {
                border = getNoFocusBorder();
            }
            cb.setBorder(border);

            return cb;
         }

      });
   }
   
   protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
   private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
   private static Border getNoFocusBorder() {
       if (System.getSecurityManager() != null) {
           return SAFE_NO_FOCUS_BORDER;
       } else {
           return noFocusBorder;
       }
   }

   class MyListModel extends AbstractListModel
   {
      public int getSize() { return options.length; }
      public Object getElementAt(int index) { return options[index]; }
      public void changed(int index) { fireContentsChanged(MultiListPicker.this, index, index); }
   }

}

