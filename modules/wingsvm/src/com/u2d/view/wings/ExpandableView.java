package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import org.wings.*;
import org.wings.border.SEmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.ImageIcon;


/**
 * @author Eitan Suez
 */
public class ExpandableView extends SPanel implements ComplexEView, Editor
{
   private ComplexEObject _ceo;
   private ExpandCollapseButton _toggleBtn;
   private ListItemView _handle;
   private FormView _leaf;
   private GridBagConstraints cc = new GridBagConstraints();

   public ExpandableView()
   {
      setLayout(new SGridBagLayout());

      cc.gridx = 0; cc.gridy = 0; cc.gridwidth = 1; cc.gridheight = 1;
      _toggleBtn = new ExpandCollapseButton();
      add(_toggleBtn, cc);
      _toggleBtn.addActionListener( new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               expandCollapse(evt.getActionCommand());
            }
         });
   }

   public void bind(ComplexEObject ceo)
   {
      boolean expanded = (ceo.field() == null) || defaultExpand(ceo);
      bind(ceo, expanded);
   }

   private boolean defaultExpand(ComplexEObject ceo)
   {
      return ceo.field().required() && ceo.isEditableState();
   }

   public void bind(ComplexEObject ceo, boolean expanded)
   {
      _ceo = ceo;

      _handle = (ListItemView) _ceo.getListItemView();
      cc.gridx = 1; cc.gridy = 0;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      add((SComponent) _handle, cc);

      if (_toggleBtn.isExpanded() != expanded)
         _toggleBtn.doClick();
   }

   public void detach()
   {
      _handle.detach();
      remove(_handle);

      if (_leaf != null)
      {
         expandCollapse(false);
         _leaf.detach();
         _leaf = null; // for now, until formviews become poolable
      }
   }


   private FormView leaf()
   {
      if (_leaf == null) _leaf = new FormView(_ceo, true);
      return _leaf;
   }

   public int transferValue() { return leaf().transferValue(); }
   public int validateValue() { return leaf().validateValue(); }

   public void setEditable(boolean editable) { leaf().setEditable(editable); }
   public boolean isEditable() { return leaf().isEditable(); }

   public boolean isExpanded() { return _toggleBtn.isExpanded(); }

   private void expandCollapse(String which)
   {
      expandCollapse("+".equals(which));
   }

   private synchronized void expandCollapse(boolean expand)
   {
      if (expand && _toggleBtn.isCollapsed())
      {
         cc.gridx = 1; cc.gridy = 1;
         cc.gridwidth = GridBagConstraints.REMAINDER;
         add(leaf(), cc);
      }
      else if (!expand && _toggleBtn.isExpanded())
      {
         remove(_leaf);
      }
      _toggleBtn.toggle(expand);
   }

   class ExpandCollapseButton extends SButton
   {
      ExpandCollapseButton()
      {
//         setOpaque(false);
         setIcon(new SImageIcon(EXPAND_ICON));
         setActionCommand("+");
         setBorder(new SEmptyBorder(1,1,1,1));

//         setContentAreaFilled(false);
         // causes no painting of background of button when pressed
         // icon change is now visual cue of press so no need for content
         // area filling (looks kind of out of place when set to true)

//         setFocusPainted(false);
//         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      void toggle()
      {
         String newActionCommand = (isCollapsed()) ? "-" : "+";
         ImageIcon icon = (isCollapsed()) ? COLLAPSE_ICON : EXPAND_ICON;
         setIcon(new SImageIcon(icon));
         setActionCommand(newActionCommand);
      }

      void toggle(boolean expanded)
      {
         if (expanded != isExpanded())
            toggle();
      }

      boolean isExpanded() { return "-".equals(getActionCommand()); }
      boolean isCollapsed() { return !isExpanded(); }
      String getState() { return getActionCommand(); }
   }

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return true; }
   public void propertyChange(java.beans.PropertyChangeEvent evt) { }
   public void stateChanged(ChangeEvent evt) { }


   static ImageIcon EXPAND_ICON, COLLAPSE_ICON;
   static ImageIcon EXPAND_ROLLOVER, COLLAPSE_ROLLOVER;
   static
   {
      ClassLoader loader = ExpandableView.class.getClassLoader();
      java.net.URL imgURL = loader.getResource("images/expand.gif");
      EXPAND_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/collapse.gif");
      COLLAPSE_ICON = new ImageIcon(imgURL);
   }


}
