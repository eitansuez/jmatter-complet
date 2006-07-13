/*
 * Created on Dec 12, 2003
 */
package com.u2d.view.swing;

import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class GenericFrame extends CloseableJInternalFrame
{
   private transient View _view;
   
   public GenericFrame(View view)
   {
      this(view, view.withTitlePane());
   }
   
   public GenericFrame(View view, boolean withTitlePane)
   {
      super();
      _view = view;
      
      setTitle(view.getTitle());
      Icon icon = view.iconSm();
      if (icon != null)
         setFrameIcon(icon);

      JPanel contentPane = (JPanel) getContentPane();

      if (withTitlePane)
      {
         GenericTitleView titleView = new GenericTitleView(_view);
         contentPane.add((JComponent) titleView, BorderLayout.NORTH);
      }
      JScrollPane scrollPane = new JScrollPane((JComponent) _view);
      contentPane.add(scrollPane, BorderLayout.CENTER);
      
      setResizable(true); setMaximizable(true); setIconifiable(true); setClosable(true);
      pack();
   }
   
   public View getView() { return _view; }
   
}
