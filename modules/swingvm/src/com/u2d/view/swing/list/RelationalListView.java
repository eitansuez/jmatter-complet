/*
 * Created on Oct 11, 2004
 */
package com.u2d.view.swing.list;

import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.*;
import com.u2d.field.IndexedField;
import com.u2d.list.RelationalList;
import com.u2d.model.*;
import com.u2d.ui.*;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.DropTargetHandler;

/**
 * @author Eitan Suez
 */
public class RelationalListView extends ListView
{
   private ListAssociationView _laview;
   
   public RelationalListView(RelationalList leo)
   {
      super(leo);

      Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
      border = new CompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3));
      setBorder(border);

      // serves as a handle for creating/listing child items
      NullAssociation nullEO = new NullAssociation(leo.field(), leo.parentObject());
      _laview = new ListAssociationView(nullEO);
      add(_laview);
   }
   
   public void detach()
   {
      super.detach();
      _laview.detach();
   }

   protected void addItems()
   {
      super.addItems();
      if (_laview != null)
         add(_laview);
   }


   class ListAssociationView extends JPanel implements PopupClient, ChangeListener
   {
      private EView _listItemView;
      private NullAssociation _nulleo;
      private PopupButton2 _trigger;
      private JComponent _contents;
      
      public ListAssociationView(NullAssociation nulleo)
      {
         setLayout(new FlowLayout(FlowLayout.LEFT));
         setOpaque(false);
         _nulleo = nulleo;
         _leo.parentObject().addChangeListener(this);

         _listItemView = _nulleo.getView();
         JComponent listItemComponent = (JComponent) _listItemView;
         listItemComponent.setTransferHandler(new DropTargetHandler());
         
         add(listItemComponent);
         
         _trigger = new PopupButton2(null, this, this, this);
         add(_trigger);
         
         stateChanged(null);
      }
            
      public void stateChanged(javax.swing.event.ChangeEvent evt)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _trigger.setVisible(_leo.parentObject().isEditableState());
            }
         });
      }
      

      /**
       * implementation of PopupClient.  trigger delegates to this method
       * when it's time for it to show a list of items to pick from.
       */
      public JComponent getContents()
      {
         if (_contents == null)
         {
            AbstractListEO pickList = _leo.type().Browse(null);
            ListEView listView = pickList.getPickView();
            JList list = null;
            if (listView instanceof CompositeView)
            {
               list = (JList) ((CompositeView) listView).getInnerView();
            }
            else
            {
               list = (JList) listView;
            }
            final JList jlist = list;
            list.addListSelectionListener(new ListSelectionListener()
               {
                  public void valueChanged(ListSelectionEvent evt)
                  {
                     ComplexEObject selectedItem = (ComplexEObject) jlist.getSelectedValue();
                     _nulleo.set(selectedItem);
                     _trigger.hidePopup();
                     CloseableJInternalFrame.updateSize(ListAssociationView.this);
                  }
               });
            if (listView instanceof CompositeView)
            {
               _contents = (JComponent) listView;
            }
            else
            {
               _contents = new JScrollPane(list);
            }
         }
         return _contents;
      }
      
      public void detach()
      {
         _leo.parentObject().removeChangeListener(this);
         _listItemView.detach();
      }
      
   }
   
   
}
