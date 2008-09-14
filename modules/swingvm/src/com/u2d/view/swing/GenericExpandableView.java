package com.u2d.view.swing;

import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.GradientPanel;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 18, 2008
 * Time: 5:43:18 PM
 */
public class GenericExpandableView extends JPanel
{
   protected ExpandCollapseButton _toggleBtn;
   protected JComponent _leaf;

   public GenericExpandableView()
   {
      setOpaque(false);
      setBorder(BorderFactory.createLineBorder(Color.black));

      MigLayout layout = new MigLayout("insets 0, flowy, hidemode 2, fill");
      setLayout(layout);
      
      _toggleBtn = new ExpandCollapseButton();
      _toggleBtn.addActionListener( new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               expandCollapse(evt.getActionCommand());
            }
         });
   }

   public GenericExpandableView(JComponent handle, JComponent leaf)
   {
      this(handle, leaf, new Color(0x5171FF));
   }
   public GenericExpandableView(JComponent handle, JComponent leaf, Color handlerBg)
   {
      this();
      _leaf = leaf;
      
      GradientPanel gp = new GradientPanel(handlerBg, false);
      gp.setLayout(new BorderLayout());
      gp.add(handle, BorderLayout.CENTER);
      gp.add(_toggleBtn, BorderLayout.LINE_END);
      
      add(gp, "alignx leading, aligny top, growx");
      add(leaf, "alignx leading, aligny top, grow");
      
      expandCollapse(false);
   }

   public boolean isExpanded() { return _toggleBtn.isExpanded(); }

   protected void expandCollapse(String which)
   {
      expandCollapse("+".equals(which)); 
   }

   protected synchronized void expandCollapse(boolean expand)
   {
      _leaf.setVisible(expand);
      _toggleBtn.toggle(expand);
      CloseableJInternalFrame.updateSize(this);
   }

   class ExpandCollapseButton extends JButton
   {
      ExpandCollapseButton()
      {
         setOpaque(false);
         setIcon(EXPAND_ICON);
         setActionCommand("+");
         setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
         setContentAreaFilled(false);  // causes no painting of background of button when pressed
         // icon change is now visual cue of press so no need for content
         // area filling (looks kind of out of place when set to true)
         setFocusPainted(false);
         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
   
      void toggle()
      {
         String newActionCommand = (isCollapsed()) ? "-" : "+";
         ImageIcon icon = (isCollapsed()) ? COLLAPSE_ICON : EXPAND_ICON;
         setIcon(icon);
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
   
      private Insets _insets = new Insets(2,5,2,5);
      public Insets getInsets() { return _insets; }
   }


   static ImageIcon EXPAND_ICON, COLLAPSE_ICON;
   static ImageIcon EXPAND_ROLLOVER, COLLAPSE_ROLLOVER;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/navigate_open.png");
      EXPAND_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/navigate_close.png");
      COLLAPSE_ICON = new ImageIcon(imgURL);
   }

}
