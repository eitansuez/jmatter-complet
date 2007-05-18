package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.JListView;
import com.u2d.type.composite.Folder;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.l2fprod.common.swing.JOutlookBar;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 13, 2005
 * Time: 5:34:42 PM
 */
public class OutlookFolderView extends JOutlookBar implements ComplexEView
{
   private Folder _folder;
   private List<JListView> _tabs = new ArrayList<JListView>();

   public OutlookFolderView() { }

   public OutlookFolderView(Folder folder)
   {
      this();
      bind(folder);
   }
   
   public void bind(Folder folder)
   {
      _folder = folder;

      ComplexEObject item;
      for (int i=0; i<_folder.size(); i++)
      {
         item = (ComplexEObject) _folder.get(i);
         if (item instanceof Folder)
         {
            Folder subfolder = (Folder) item;
            JListView v = new JListView(subfolder.getItems(), true);
            v.setBorder(new LineBorder(Color.black));
//            v.setOpaque(false);
            String caption = subfolder.getName().stringValue();
            addTab(caption, _folder.iconSm(), makeScrollPane(v));
            addMnemonic(_tabs.size(), caption);
            _tabs.add(v);
         }
      }
   }
   
   private String mnemonics = "";
   private void addMnemonic(int index, String caption)
   {
      int i = 0;
      char ch = caption.charAt(i++);
      while (mnemonics.indexOf(ch) >= 0 && caption.length() > i)
      {
         ch = caption.charAt(i++);
      }
      setMnemonicAt(_tabs.size(), ch);
      mnemonics += ch;
   }
   
   public void detach()
   {
      for (JListView v : _tabs)
      {
         v.detach();
      }
      _tabs.clear();
      removeAll();
   }

   public EObject getEObject() { return _folder; }
   public boolean isMinimized() { return false; }
   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}

   public void focusFirstItem()
   {
      JListView v = _tabs.get(getSelectedIndex());
      v.setSelectedIndex(0);
      v.requestFocusInWindow();
   }
}

